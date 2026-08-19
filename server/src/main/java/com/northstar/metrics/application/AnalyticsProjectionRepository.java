package com.northstar.metrics.application;

import com.northstar.metrics.domain.PortfolioMetrics;
import com.northstar.metrics.domain.PortfolioScope;
import java.util.Optional;

public interface AnalyticsProjectionRepository {
  Optional<PortfolioMetrics> findByScope(PortfolioScope scope);
}
