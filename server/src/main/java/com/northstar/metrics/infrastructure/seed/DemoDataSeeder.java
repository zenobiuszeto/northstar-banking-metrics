package com.northstar.metrics.infrastructure.seed;

import com.northstar.metrics.config.NorthstarProperties;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class DemoDataSeeder implements ApplicationRunner {
  private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
  private static final long SEED_LOCK = 6_614_947_283L;
  private final JdbcTemplate jdbc;
  private final SyntheticDataFactory factory;
  private final NorthstarProperties.Seed properties;

  DemoDataSeeder(JdbcTemplate jdbc, SyntheticDataFactory factory, NorthstarProperties properties) {
    this.jdbc = jdbc;
    this.factory = factory;
    this.properties = properties.seed();
  }

  @Override
  public void run(ApplicationArguments args) {
    if (!properties.enabled()) {
      log.info("Synthetic data seeding is disabled");
      return;
    }
    Boolean acquired = jdbc.queryForObject("select pg_try_advisory_lock(?)", Boolean.class, SEED_LOCK);
    if (!Boolean.TRUE.equals(acquired)) {
      log.info("Another instance owns the synthetic data seed lock; skipping");
      return;
    }
    try {
      seedIfEmpty();
    } finally {
      jdbc.queryForObject("select pg_advisory_unlock(?)", Boolean.class, SEED_LOCK);
    }
  }

  private void seedIfEmpty() {
    Integer existingCustomers = jdbc.queryForObject("select count(*) from customers", Integer.class);
    if (existingCustomers != null && existingCustomers > 0) {
      log.info("Database already contains {} customers; skipping synthetic seed", existingCustomers);
      return;
    }

    log.info("Generating {} synthetic customers in batches of {}", properties.customerCount(), properties.batchSize());
    batchRange(1, properties.customerCount(), factory::customer,
        "insert into customers(customer_number,segment,region,risk_score,created_at) values (?,?,?,?,?)");

    List<Long> customerIds = jdbc.queryForList("select id from customers order by id", Long.class);
    List<Object[]> accounts = new ArrayList<>(properties.batchSize());
    for (long customerId : customerIds) {
      for (int ordinal = 0; ordinal < factory.accountCount(customerId); ordinal++) {
        accounts.add(factory.account(customerId, ordinal));
        flushWhenFull(accounts, "insert into accounts(customer_id,product,status,balance,opened_at) values (?,?,?,?,?)");
      }
    }
    flush(accounts, "insert into accounts(customer_id,product,status,balance,opened_at) values (?,?,?,?,?)");

    List<Long> accountIds = jdbc.queryForList("select id from accounts order by id", Long.class);
    List<Object[]> transactions = new ArrayList<>(properties.batchSize());
    for (long accountId : accountIds) {
      for (int ordinal = 0; ordinal < factory.transactionCount(accountId); ordinal++) {
        transactions.add(factory.transaction(accountId, ordinal));
        flushWhenFull(transactions, "insert into transactions(account_id,amount,transaction_type,occurred_at,fraud_flag) values (?,?,?,?,?)");
      }
    }
    flush(transactions, "insert into transactions(account_id,amount,transaction_type,occurred_at,fraud_flag) values (?,?,?,?,?)");
    log.info("Synthetic seed complete: {} customers, {} accounts", customerIds.size(), accountIds.size());
  }

  private void batchRange(int start, int end, RowFactory rowFactory, String sql) {
    List<Object[]> rows = new ArrayList<>(properties.batchSize());
    for (int ordinal = start; ordinal <= end; ordinal++) {
      rows.add(rowFactory.create(ordinal));
      flushWhenFull(rows, sql);
    }
    flush(rows, sql);
  }

  private void flushWhenFull(List<Object[]> rows, String sql) {
    if (rows.size() >= properties.batchSize()) {
      flush(rows, sql);
    }
  }

  private void flush(List<Object[]> rows, String sql) {
    if (!rows.isEmpty()) {
      jdbc.batchUpdate(sql, rows);
      rows.clear();
    }
  }

  @FunctionalInterface
  private interface RowFactory {
    Object[] create(int ordinal);
  }
}
