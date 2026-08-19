package com.northstar.metrics.domain;

import java.time.LocalDate;
import java.util.Objects;

public record Customer(Long id, String customerNumber, Segment segment, String region,
                       int riskScore, LocalDate createdAt) {
  public Customer {
    if (customerNumber == null || customerNumber.isBlank()) throw new IllegalArgumentException("Customer number is required");
    Objects.requireNonNull(segment); Objects.requireNonNull(createdAt);
    if (region == null || region.isBlank()) throw new IllegalArgumentException("Customer region is required");
    if (riskScore < 300 || riskScore > 850) throw new IllegalArgumentException("Risk score must be between 300 and 850");
  }
  public enum Segment { BUSINESS, CONSUMER }
}
