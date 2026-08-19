package com.northstar.metrics.domain;

import java.time.Instant;
import java.util.Objects;

public record BankingTransaction(Long id, long accountId, Money amount, Type type,
                                 Instant occurredAt, boolean fraudFlag) {
  public BankingTransaction {
    if (accountId <= 0) throw new IllegalArgumentException("Transaction account id must be positive");
    Objects.requireNonNull(amount); Objects.requireNonNull(type); Objects.requireNonNull(occurredAt);
    if (amount.amount().signum() <= 0) throw new IllegalArgumentException("Transaction amount must be positive");
  }
  public enum Type { DEPOSIT, PAYMENT, WITHDRAWAL, INTERNAL_TRANSFER }
  public boolean isExternalFlow() { return type != Type.INTERNAL_TRANSFER; }
}
