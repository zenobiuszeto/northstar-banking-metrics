package com.northstar.metrics.application;

public class MetricsNotFoundException extends RuntimeException {
  public MetricsNotFoundException(String product) {
    super("Metrics are not available for product: " + product);
  }
}
