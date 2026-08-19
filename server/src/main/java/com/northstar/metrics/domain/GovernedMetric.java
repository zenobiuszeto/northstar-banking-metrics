package com.northstar.metrics.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class GovernedMetric {
  private GovernedMetric() {}
  public record Definition(String code, String formulaVersion, String owner, Certification certification,
                           Instant effectiveAt, String lineage) {
    public Definition {
      if (code == null || code.isBlank() || formulaVersion == null || formulaVersion.isBlank())
        throw new IllegalArgumentException("Metric code and formula version are required");
      Objects.requireNonNull(owner); Objects.requireNonNull(certification); Objects.requireNonNull(effectiveAt);
      if (lineage == null || lineage.isBlank()) throw new IllegalArgumentException("Metric lineage is required");
    }
  }
  public record DailyValue(String metricCode, Product.Code productCode, LocalDate metricDate,
                           BigDecimal value, String formulaVersion, Instant calculatedAt, Instant sourceWatermark) {
    public DailyValue {
      Objects.requireNonNull(metricCode); Objects.requireNonNull(productCode); Objects.requireNonNull(metricDate);
      Objects.requireNonNull(value); Objects.requireNonNull(formulaVersion); Objects.requireNonNull(calculatedAt); Objects.requireNonNull(sourceWatermark);
      if (sourceWatermark.isAfter(calculatedAt)) throw new IllegalArgumentException("Source watermark cannot follow calculation time");
    }
  }
  public enum Certification { DEMO, DRAFT, CERTIFIED }
}
