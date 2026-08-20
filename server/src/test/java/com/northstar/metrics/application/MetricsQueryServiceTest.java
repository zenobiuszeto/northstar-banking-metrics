package com.northstar.metrics.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.northstar.metrics.domain.PortfolioMetrics;
import com.northstar.metrics.domain.PortfolioScope;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricsQueryServiceTest {
  @Mock AnalyticsProjectionRepository repository;

  @Test
  void queriesTheRepositoryWithAValidatedProduct() {
    PortfolioMetrics expected = new PortfolioMetrics(PortfolioScope.ALL, 100_000, 1, BigDecimal.ONE,
        BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN,
        2, BigDecimal.ONE, BigDecimal.TEN);
    when(repository.findByScope(PortfolioScope.ALL)).thenReturn(Optional.of(expected));

    assertThat(new MetricsQueryService(repository).overview("All products")).isSameAs(expected);
  }
}
