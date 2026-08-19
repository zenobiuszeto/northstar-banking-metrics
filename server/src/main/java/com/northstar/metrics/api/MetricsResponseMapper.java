package com.northstar.metrics.api;

import com.northstar.metrics.domain.PortfolioMetrics;
import java.time.Clock;
import org.springframework.stereotype.Component;

@Component
public class MetricsResponseMapper {
  private final Clock clock;

  public MetricsResponseMapper(Clock clock) {
    this.clock = clock;
  }

  public MetricsResponse toResponse(PortfolioMetrics metrics) {
    return new MetricsResponse(metrics.scope().displayName(), metrics.customerCount(), metrics.applications(),
        metrics.approvalRate(), metrics.depositsMillions(), metrics.fraudRate(),
        metrics.monthlyChurnRate(), metrics.retentionRate(), metrics.lifetimeValue(),
        metrics.atRiskCustomers(), metrics.depositsAtRiskMillions(), metrics.winBackRate(), clock.instant());
  }
}
