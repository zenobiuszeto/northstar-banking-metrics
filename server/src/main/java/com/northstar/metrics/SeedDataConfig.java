package com.northstar.metrics;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
class SeedDataConfig {
  private static final int CUSTOMER_COUNT = 100_000;
  private static final String[] PRODUCTS = {"Business Checking", "Consumer Checking", "Business Savings", "Consumer Savings"};
  private static final String[] REGIONS = {"West", "Southwest", "Midwest", "Northeast", "Southeast"};

  @Bean CommandLineRunner seedDatabase(JdbcTemplate jdbc, @Value("${app.seed.enabled:true}") boolean enabled) {
    return args -> {
      if (!enabled || jdbc.queryForObject("select count(*) from customers", Integer.class) > 0) return;
      ThreadLocalRandom r = ThreadLocalRandom.current();
      List<Object[]> customers = new ArrayList<>(1000);
      for (int i = 1; i <= CUSTOMER_COUNT; i++) {
        customers.add(new Object[]{"CUS-%06d".formatted(i), i % 8 == 0 ? "Business" : "Consumer", REGIONS[i % REGIONS.length], r.nextInt(300, 851), LocalDate.now().minusDays(r.nextInt(30, 1460))});
        if (customers.size() == 1000) { jdbc.batchUpdate("insert into customers(customer_number,segment,region,risk_score,created_at) values (?,?,?,?,?)", customers); customers.clear(); }
      }
      if (!customers.isEmpty()) jdbc.batchUpdate("insert into customers(customer_number,segment,region,risk_score,created_at) values (?,?,?,?,?)", customers);
      List<Object[]> accounts = new ArrayList<>(1000);
      List<Object[]> transactions = new ArrayList<>(1000);
      for (long customerId = 1; customerId <= CUSTOMER_COUNT; customerId++) {
        int accountCount = customerId % 9 == 0 ? 2 : 1;
        for (int a = 0; a < accountCount; a++) {
          String product = PRODUCTS[(int)((customerId + a) % PRODUCTS.length)];
          BigDecimal balance = BigDecimal.valueOf(r.nextDouble(100, product.startsWith("Business") ? 250_000 : 45_000)).setScale(2, BigDecimal.ROUND_HALF_UP);
          accounts.add(new Object[]{customerId, product, "OPEN", balance, LocalDate.now().minusDays(r.nextInt(15, 1200))});
          if (accounts.size() == 1000) { jdbc.batchUpdate("insert into accounts(customer_id,product,status,balance,opened_at) values (?,?,?,?,?)", accounts); accounts.clear(); }
        }
      }
      if (!accounts.isEmpty()) jdbc.batchUpdate("insert into accounts(customer_id,product,status,balance,opened_at) values (?,?,?,?,?)", accounts);
      Long maxAccount = jdbc.queryForObject("select max(id) from accounts", Long.class);
      for (long accountId = 1; accountId <= maxAccount; accountId++) {
        int transactionCount = 3 + (int)(accountId % 5);
        for (int t = 0; t < transactionCount; t++) {
          boolean deposit = t % 3 == 0;
          transactions.add(new Object[]{accountId, BigDecimal.valueOf(r.nextDouble(10, 12_000)).setScale(2, BigDecimal.ROUND_HALF_UP), deposit ? "DEPOSIT" : "PAYMENT", Timestamp.valueOf(LocalDateTime.now().minusDays(r.nextInt(0, 365))), r.nextInt(10_000) < 42});
          if (transactions.size() == 1000) { jdbc.batchUpdate("insert into transactions(account_id,amount,transaction_type,occurred_at,fraud_flag) values (?,?,?,?,?)", transactions); transactions.clear(); }
        }
      }
      if (!transactions.isEmpty()) jdbc.batchUpdate("insert into transactions(account_id,amount,transaction_type,occurred_at,fraud_flag) values (?,?,?,?,?)", transactions);
    };
  }
}
