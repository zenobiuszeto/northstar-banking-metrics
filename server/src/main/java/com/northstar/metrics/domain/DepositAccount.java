package com.northstar.metrics.domain;

import java.time.LocalDate;
import java.util.Objects;

public record DepositAccount(Long id, long customerId, Product.Code productCode, Status status,
                             Money balance, LocalDate openedAt, LocalDate closedAt) {
  public DepositAccount {
    if (customerId <= 0) throw new IllegalArgumentException("Account customer id must be positive");
    Objects.requireNonNull(productCode); Objects.requireNonNull(status); Objects.requireNonNull(balance); Objects.requireNonNull(openedAt);
    if (balance.isNegative()) throw new IllegalArgumentException("Deposit account balance cannot be negative");
    if (status == Status.CLOSED && closedAt == null) throw new IllegalArgumentException("Closed accounts require a close date");
    if (closedAt != null && closedAt.isBefore(openedAt)) throw new IllegalArgumentException("Close date cannot precede open date");
  }
  public enum Status { OPEN, DORMANT, CLOSED }
  public DepositAccount close(LocalDate date) { return new DepositAccount(id, customerId, productCode, Status.CLOSED, balance, openedAt, date); }
}
