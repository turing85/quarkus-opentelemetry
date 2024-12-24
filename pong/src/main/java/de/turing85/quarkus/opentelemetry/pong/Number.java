package de.turing85.quarkus.opentelemetry.pong;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Jacksonized
@Builder
@ToString
public class Number {
  private final Long id;
  private final long value;
}
