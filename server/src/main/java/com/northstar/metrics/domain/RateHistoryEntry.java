package com.northstar.metrics.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record RateHistoryEntry(Product.Code productCode, Instant effectiveAt, BigDecimal annualRate,
                               String offerCode, BigDecimal benchmarkRate) {
  public RateHistoryEntry {
    Objects.requireNonNull(productCode); Objects.requireNonNull(effectiveAt); Objects.requireNonNull(annualRate); Objects.requireNonNull(benchmarkRate);
    if (annualRate.signum() < 0 || benchmarkRate.signum() < 0) throw new IllegalArgumentException("Rates cannot be negative");
  }
}
