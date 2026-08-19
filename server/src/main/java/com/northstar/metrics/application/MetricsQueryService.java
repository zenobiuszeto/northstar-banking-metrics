package com.northstar.metrics.application;

import com.northstar.metrics.domain.PortfolioMetrics;
import com.northstar.metrics.domain.PortfolioScope;
import org.springframework.stereotype.Service;

@Service
public class MetricsQueryService {
  private final AnalyticsProjectionRepository repository;

  public MetricsQueryService(AnalyticsProjectionRepository repository) {
    this.repository = repository;
  }

  public PortfolioMetrics overview(String productName) {
    PortfolioScope scope = PortfolioScope.fromDisplayName(productName);
    return repository.findByScope(scope)
        .orElseThrow(() -> new MetricsNotFoundException(scope.displayName()));
  }
}
