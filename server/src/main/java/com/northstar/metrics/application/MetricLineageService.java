package com.northstar.metrics.application;

import com.northstar.metrics.domain.GovernedMetric;
import org.springframework.stereotype.Service;

@Service
public class MetricLineageService {
  private final MetricDefinitionRepository definitions;
  public MetricLineageService(MetricDefinitionRepository definitions) { this.definitions = definitions; }
  public GovernedMetric.Definition describe(String metricCode) {
    if (metricCode == null || metricCode.isBlank()) throw new IllegalArgumentException("Metric code is required");
    return definitions.findByCode(metricCode.strip().toUpperCase())
        .orElseThrow(() -> new MetricsNotFoundException(metricCode));
  }
}
