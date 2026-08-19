package com.northstar.metrics.infrastructure.metrics;

import com.northstar.metrics.application.AnalyticsProjectionRepository;
import com.northstar.metrics.domain.PortfolioMetrics;
import com.northstar.metrics.domain.PortfolioScope;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class DemoMetricsRepository implements AnalyticsProjectionRepository {
  private final Map<PortfolioScope, PortfolioMetrics> metrics;

  public DemoMetricsRepository() {
    metrics = new EnumMap<>(PortfolioScope.class);
    metrics.put(PortfolioScope.ALL, row(PortfolioScope.ALL, 18_462, "71.8", "486.2", ".42", "1.9", "86.4", "1246"));
    metrics.put(PortfolioScope.BUSINESS_CHECKING, row(PortfolioScope.BUSINESS_CHECKING, 4_218, "74.2", "184.6", ".31", "1.1", "91.2", "3210"));
    metrics.put(PortfolioScope.CONSUMER_CHECKING, row(PortfolioScope.CONSUMER_CHECKING, 6_774, "70.3", "126.4", ".49", "2.4", "83.8", "880"));
    metrics.put(PortfolioScope.BUSINESS_SAVINGS, row(PortfolioScope.BUSINESS_SAVINGS, 2_391, "76.1", "87.5", ".27", ".9", "93.5", "2890"));
    metrics.put(PortfolioScope.CONSUMER_SAVINGS, row(PortfolioScope.CONSUMER_SAVINGS, 5_079, "69.7", "87.7", ".53", "2.1", "85.4", "730"));
  }

  @Override
  public Optional<PortfolioMetrics> findByScope(PortfolioScope scope) {
    return Optional.ofNullable(metrics.get(scope));
  }

  private PortfolioMetrics row(PortfolioScope scope, int applications, String approvalRate,
      String deposits, String fraud, String churn, String retention, String lifetimeValue) {
    return new PortfolioMetrics(scope, 100_000, applications, decimal(approvalRate), decimal(deposits),
        decimal(fraud), decimal(churn), decimal(retention), decimal(lifetimeValue), 4_218,
        decimal("9.6"), decimal("42.1"));
  }

  private BigDecimal decimal(String value) {
    return new BigDecimal(value);
  }
}
