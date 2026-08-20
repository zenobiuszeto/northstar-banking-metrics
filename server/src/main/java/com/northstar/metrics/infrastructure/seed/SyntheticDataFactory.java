package com.northstar.metrics.infrastructure.seed;

import com.northstar.metrics.domain.BankingTransaction;
import com.northstar.metrics.domain.Customer;
import com.northstar.metrics.domain.DepositAccount;
import com.northstar.metrics.domain.Money;
import com.northstar.metrics.domain.Product;
import com.northstar.metrics.domain.ProductApplication;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Instant;
import java.util.SplittableRandom;
import org.springframework.stereotype.Component;

@Component
class SyntheticDataFactory {
  private static final Product.Code[] PRODUCTS = {
      Product.Code.BUSINESS_CHECKING, Product.Code.CONSUMER_CHECKING,
      Product.Code.BUSINESS_SAVINGS, Product.Code.CONSUMER_SAVINGS
  };
  private static final String[] REGIONS = {"West", "Southwest", "Midwest", "Northeast", "Southeast"};
  private final Clock clock;

  SyntheticDataFactory(Clock clock) {
    this.clock = clock;
  }

  Customer customer(int ordinal) {
    SplittableRandom random = random(ordinal, 11);
    return new Customer(null, "CUS-%06d".formatted(ordinal),
        ordinal % 8 == 0 ? Customer.Segment.BUSINESS : Customer.Segment.CONSUMER,
        REGIONS[ordinal % REGIONS.length], random.nextInt(300, 851),
        LocalDate.now(clock).minusDays(random.nextInt(30, 1460)));
  }

  int accountCount(long customerId) {
    return customerId % 9 == 0 ? 2 : 1;
  }

  DepositAccount account(long customerId, int ordinal) {
    SplittableRandom random = random(customerId, ordinal + 23);
    Product.Code product = PRODUCTS[(int) ((customerId + ordinal) % PRODUCTS.length)];
    double upperBound = product.name().startsWith("BUSINESS") ? 250_000 : 45_000;
    return new DepositAccount(null, customerId, product, DepositAccount.Status.OPEN,
        Money.usd(money(random.nextDouble(100, upperBound))),
        LocalDate.now(clock).minusDays(random.nextInt(15, 1200)), null);
  }

  int transactionCount(long accountId) {
    return 3 + (int) (accountId % 5);
  }

  BankingTransaction transaction(long accountId, int ordinal) {
    SplittableRandom random = random(accountId, ordinal + 47);
    boolean deposit = ordinal % 3 == 0;
    Instant occurredAt = clock.instant().minusSeconds(random.nextLong(0, 365L * 86_400));
    return new BankingTransaction(null, accountId, Money.usd(money(random.nextDouble(10, 12_000))),
        deposit ? BankingTransaction.Type.DEPOSIT : BankingTransaction.Type.PAYMENT,
        occurredAt, random.nextInt(10_000) < 42);
  }

  ProductApplication application(long customerId) {
    SplittableRandom random = random(customerId, 71);
    Instant submitted = clock.instant().minusSeconds(random.nextLong(10L * 86_400, 365L * 86_400));
    Instant decisioned = submitted.plusSeconds(random.nextLong(3_600, 72L * 3_600));
    boolean funded = customerId % 5 != 0;
    return new ProductApplication(null, customerId, PRODUCTS[(int) (customerId % PRODUCTS.length)],
        customerId % 4 == 0 ? ProductApplication.Channel.BRANCH : ProductApplication.Channel.DIGITAL,
        funded ? ProductApplication.Status.FUNDED : ProductApplication.Status.DECLINED,
        submitted, decisioned, funded ? decisioned.plusSeconds(random.nextLong(86_400, 8L * 86_400)) : null);
  }

  private SplittableRandom random(long identity, long salt) {
    return new SplittableRandom(identity * 31 + salt);
  }

  private BigDecimal money(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }
}
