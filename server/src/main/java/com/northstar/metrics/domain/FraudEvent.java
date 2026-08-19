package com.northstar.metrics.domain;

import java.time.Instant;
import java.util.Objects;

public record FraudEvent(Long id, Long applicationId, Long transactionId, Type type, Status status,
                         Money suspectedAmount, Money confirmedLoss, Instant detectedAt) {
  public FraudEvent {
    if (applicationId == null && transactionId == null) throw new IllegalArgumentException("Fraud event requires an application or transaction");
    Objects.requireNonNull(type); Objects.requireNonNull(status); Objects.requireNonNull(suspectedAmount); Objects.requireNonNull(confirmedLoss); Objects.requireNonNull(detectedAt);
    if (suspectedAmount.isNegative() || confirmedLoss.isNegative()) throw new IllegalArgumentException("Fraud amounts cannot be negative");
    if (confirmedLoss.amount().compareTo(suspectedAmount.amount()) > 0) throw new IllegalArgumentException("Confirmed loss cannot exceed suspected amount");
  }
  public enum Type { APPLICATION_FRAUD, PAYMENT_FRAUD, ACCOUNT_TAKEOVER }
  public enum Status { OPEN, CONFIRMED, DISMISSED, RECOVERED }
}
