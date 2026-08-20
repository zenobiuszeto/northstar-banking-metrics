package com.northstar.metrics.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.northstar.metrics.domain.GovernedMetric;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MetricLineageServiceTest {
  @Test void normalizesMetricCodesBeforeQueryingDefinitions() {
    MetricDefinitionRepository repository = org.mockito.Mockito.mock(MetricDefinitionRepository.class);
    GovernedMetric.Definition definition = new GovernedMetric.Definition("TOTAL_DEPOSITS", "v1", "Finance",
        GovernedMetric.Certification.DRAFT, Instant.EPOCH, "daily_account_snapshots.ledger_balance");
    when(repository.findByCode("TOTAL_DEPOSITS")).thenReturn(Optional.of(definition));
    assertThat(new MetricLineageService(repository).describe(" total_deposits ")).isSameAs(definition);
  }
}
