package com.northstar.metrics.application;

import com.northstar.metrics.domain.GovernedMetric;
import java.util.Optional;

public interface MetricDefinitionRepository {
  Optional<GovernedMetric.Definition> findByCode(String metricCode);
}
