package com.northstar.metrics.infrastructure.seed;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.metrics.domain.ProductApplication;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class SyntheticDataFactoryTest {
  private final SyntheticDataFactory factory = new SyntheticDataFactory(
      Clock.fixed(Instant.parse("2026-08-19T12:00:00Z"), ZoneOffset.UTC));

  @Test void generationIsDeterministicAndUsesDomainObjects() {
    assertThat(factory.customer(42)).isEqualTo(factory.customer(42));
    assertThat(factory.account(42, 0).customerId()).isEqualTo(42);
    assertThat(factory.transaction(42, 0).occurredAt()).isBeforeOrEqualTo(Instant.parse("2026-08-19T12:00:00Z"));
  }

  @Test void generatedApplicationRespectsLifecycleInvariants() {
    ProductApplication application = factory.application(7);
    assertThat(application.decisionedAt()).isAfter(application.submittedAt());
    if (application.status() == ProductApplication.Status.FUNDED)
      assertThat(application.fundedAt()).isAfter(application.decisionedAt());
  }
}
