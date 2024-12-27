package de.turing85.quarkus.opentelemetry.ping;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkiverse.reactive.messaging.nats.jetstream.client.api.PublishMessageMetadata;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

@Path(Endpoint.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Endpoint {
  public static final String PATH = "numbers";
  private final NumberRepository numberRepository;
  private final Emitter<Number> emitter;

  public Endpoint(NumberRepository numberRepository, @Channel("numbers") Emitter<Number> emitter) {
    this.numberRepository = numberRepository;
    this.emitter = emitter;
  }

  @GET
  public Uni<List<Number>> findAll() {
    return numberRepository.findAll().list();
  }

  @POST
  @WithTransaction
  public Uni<Response> createNumber(long value) {
    // @formatter:off
    return Uni
        .createFrom().item(Number.of(value))
        .onItem().call(numberRepository::persist)
        .onItem().transform(this::emitCreated)
        .onItem().transform(number -> Response
            .created(URI.create("%s/%d".formatted(PATH, number.getValue())))
            .entity(number)
            .build());
    // @formatter:on
  }

  @DELETE
  @WithTransaction
  public Uni<Response> deleteAll() {
    // @formatter:off
    return numberRepository.findAll().list()
        .onItem().call(numbers -> numberRepository.deleteAll().replaceWith(numbers))
        .onItem().transform(this::emitDeleted)
        .onItem().transform(numbers -> Response.ok(numbers).build());
    // @formatter:on
  }

  @DELETE
  @WithTransaction
  @Path("{value}")
  public Uni<Response> delete(@PathParam("value") long value) {
    // @formatter:off
    return numberRepository.find("value", value).list()
        .onItem().transformToMulti(Multi.createFrom()::iterable)
        .onItem()
            .transformToUni(number -> numberRepository.delete(number).replaceWith(number))
            .concatenate()
        .collect().asList()
        .onItem().transform(this::emitDeleted)
        .onItem().transform(numbers -> Response.ok(numbers).build());
    // @formatter:on
  }

  @GET
  @Path("{value}")
  public Uni<Response> findById(@PathParam("value") long value) {
    // @formatter:off
    return numberRepository.find("value", value).list()
        .onItem().transform(numbers -> Response.ok(numbers).build());
    // @formatter:on
  }

  private Number emitCreated(Number number) {
    return emit(number, "created");
  }

  private List<Number> emitDeleted(List<Number> numbers) {
    // @formatter:off
    return numbers.stream()
        .map(this::emitDeleted)
        .toList();
    // @formatter:on
  }

  private Number emitDeleted(Number number) {
    return emit(number, "deleted");
  }

  private Number emit(Number number, String subject) {
    // @formatter:off
    emitter.send(Message.of(
        number,
        Metadata.of(PublishMessageMetadata.builder().subject(subject).build())));
    // @formatter:on
    return number;
  }
}
