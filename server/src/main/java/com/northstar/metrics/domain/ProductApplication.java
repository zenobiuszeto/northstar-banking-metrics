package com.northstar.metrics.domain;

import java.time.Instant;
import java.util.Objects;

public record ProductApplication(Long id, long customerId, Product.Code productCode, Channel channel,
                                 Status status, Instant submittedAt, Instant decisionedAt, Instant fundedAt) {
  public ProductApplication {
    if (customerId <= 0) throw new IllegalArgumentException("Application customer id must be positive");
    Objects.requireNonNull(productCode); Objects.requireNonNull(channel); Objects.requireNonNull(status); Objects.requireNonNull(submittedAt);
    if ((status == Status.APPROVED || status == Status.DECLINED || status == Status.FUNDED) && decisionedAt == null)
      throw new IllegalArgumentException("Decisioned applications require a decision timestamp");
    if (status == Status.FUNDED && fundedAt == null) throw new IllegalArgumentException("Funded applications require a funding timestamp");
    if (decisionedAt != null && decisionedAt.isBefore(submittedAt)) throw new IllegalArgumentException("Decision cannot precede submission");
    if (fundedAt != null && (decisionedAt == null || fundedAt.isBefore(decisionedAt))) throw new IllegalArgumentException("Funding cannot precede approval");
  }
  public enum Channel { BRANCH, DIGITAL, PARTNER }
  public enum Status { SUBMITTED, APPROVED, DECLINED, FUNDED }
}
