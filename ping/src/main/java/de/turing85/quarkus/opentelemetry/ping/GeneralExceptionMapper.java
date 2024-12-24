package de.turing85.quarkus.opentelemetry.ping;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Provider
@Log4j2
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
  @Override
  public Response toResponse(Exception exception) {
    log.error(exception);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(ErrorDto.of("Internal Server Error")).build();
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static class ErrorDto {
    private final String message;

    public static ErrorDto of(String message) {
      return new ErrorDto(message);
    }
  }
}
