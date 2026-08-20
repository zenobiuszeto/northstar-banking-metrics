package com.northstar.metrics.infrastructure.seed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.SplittableRandom;
import org.springframework.stereotype.Component;

@Component
class SyntheticDataFactory {
  private static final String[] PRODUCTS = {
      "BUSINESS_CHECKING", "CONSUMER_CHECKING", "BUSINESS_SAVINGS", "CONSUMER_SAVINGS"
  };
  private static final String[] REGIONS = {"West", "Southwest", "Midwest", "Northeast", "Southeast"};
  private final Clock clock;

  SyntheticDataFactory(Clock clock) {
    this.clock = clock;
  }

  Object[] customer(int ordinal) {
    SplittableRandom random = random(ordinal, 11);
    return new Object[] {
        "CUS-%06d".formatted(ordinal), ordinal % 8 == 0 ? "BUSINESS" : "CONSUMER",
        REGIONS[ordinal % REGIONS.length], random.nextInt(300, 851),
        LocalDate.now(clock).minusDays(random.nextInt(30, 1460))
    };
  }

  int accountCount(long customerId) {
    return customerId % 9 == 0 ? 2 : 1;
  }

  Object[] account(long customerId, int ordinal) {
    SplittableRandom random = random(customerId, ordinal + 23);
    String product = PRODUCTS[(int) ((customerId + ordinal) % PRODUCTS.length)];
    double upperBound = product.startsWith("BUSINESS") ? 250_000 : 45_000;
    return new Object[] {customerId, product, "OPEN", money(random.nextDouble(100, upperBound)),
        LocalDate.now(clock).minusDays(random.nextInt(15, 1200))};
  }

  int transactionCount(long accountId) {
    return 3 + (int) (accountId % 5);
  }

  Object[] transaction(long accountId, int ordinal) {
    SplittableRandom random = random(accountId, ordinal + 47);
    boolean deposit = ordinal % 3 == 0;
    LocalDateTime occurredAt = LocalDateTime.now(clock).minusDays(random.nextInt(0, 365));
    return new Object[] {accountId, money(random.nextDouble(10, 12_000)),
        deposit ? "DEPOSIT" : "PAYMENT", Timestamp.valueOf(occurredAt), random.nextInt(10_000) < 42};
  }

  Object[] application(long customerId) {
    SplittableRandom random = random(customerId, 71);
    LocalDateTime submitted = LocalDateTime.now(clock).minusDays(random.nextInt(10, 365));
    LocalDateTime decisioned = submitted.plusHours(random.nextInt(1, 72));
    boolean funded = customerId % 5 != 0;
    return new Object[] {customerId, PRODUCTS[(int) (customerId % PRODUCTS.length)],
        customerId % 4 == 0 ? "BRANCH" : "DIGITAL", funded ? "FUNDED" : "DECLINED",
        Timestamp.valueOf(submitted), Timestamp.valueOf(decisioned),
        funded ? Timestamp.valueOf(decisioned.plusDays(random.nextInt(1, 8))) : null};
  }

  private SplittableRandom random(long identity, long salt) {
    return new SplittableRandom(identity * 31 + salt);
  }

  private BigDecimal money(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }
}
