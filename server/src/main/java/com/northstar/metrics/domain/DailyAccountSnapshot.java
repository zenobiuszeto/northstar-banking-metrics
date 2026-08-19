package com.northstar.metrics.domain;

import java.time.LocalDate;
import java.util.Objects;

public record DailyAccountSnapshot(long accountId, LocalDate snapshotDate, Money ledgerBalance,
                                   Money averageBalance, int transactionCount, Money inflows,
                                   Money outflows, boolean churnRisk) {
  public DailyAccountSnapshot {
    if (accountId <= 0) throw new IllegalArgumentException("Snapshot account id must be positive");
    Objects.requireNonNull(snapshotDate); Objects.requireNonNull(ledgerBalance); Objects.requireNonNull(averageBalance);
    Objects.requireNonNull(inflows); Objects.requireNonNull(outflows);
    if (ledgerBalance.isNegative() || averageBalance.isNegative() || inflows.isNegative() || outflows.isNegative())
      throw new IllegalArgumentException("Snapshot amounts cannot be negative");
    if (transactionCount < 0) throw new IllegalArgumentException("Transaction count cannot be negative");
  }
}
