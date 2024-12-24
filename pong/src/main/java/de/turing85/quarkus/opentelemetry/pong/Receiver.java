package de.turing85.quarkus.opentelemetry.pong;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkiverse.reactive.messaging.nats.jetstream.client.api.SubscribeMessageMetadata;
import lombok.extern.log4j.Log4j2;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
@Log4j2
public class Receiver {
  @Incoming("numbers")
  @WithSpan("numbers-receive")
  public CompletionStage<Void> data(Message<Number> message) {
    // @formatter:off
    log.info(
        "received a {} message for {}",
        message
            .getMetadata(SubscribeMessageMetadata.class)
            .map(SubscribeMessageMetadata::subject)
            .orElse("<none>"),
        message.getPayload());
    // @formatter:on
    return message.ack();
  }
}
