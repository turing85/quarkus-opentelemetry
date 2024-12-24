package de.turing85.quarkus.opentelemetry.ping;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;

@ApplicationScoped
public class NumberRepository implements PanacheRepository<Number> {
}
