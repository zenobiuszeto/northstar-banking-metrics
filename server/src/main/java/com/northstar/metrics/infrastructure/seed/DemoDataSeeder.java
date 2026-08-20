package com.northstar.metrics.infrastructure.seed;

import com.northstar.metrics.config.NorthstarProperties;
import com.northstar.metrics.domain.BankingTransaction;
import com.northstar.metrics.domain.Customer;
import com.northstar.metrics.domain.DepositAccount;
import com.northstar.metrics.domain.ProductApplication;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Component
@Profile("demo")
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

    seedReferenceData();
    log.info("Generating {} synthetic customers in batches of {}", properties.customerCount(), properties.batchSize());
    batchRange(1, properties.customerCount(), ordinal -> customerRow(factory.customer(ordinal)),
        "insert into customers(customer_number,segment,region,risk_score,created_at) values (?,?,?,?,?)");

    List<Long> customerIds = jdbc.queryForList("select id from customers order by id", Long.class);
    List<Object[]> applications = new ArrayList<>(properties.batchSize());
    int applicationCount = Math.min(customerIds.size(), 18_462);
    int generatedApplications = 0;
    for (long customerId : customerIds) {
      if (generatedApplications++ >= applicationCount) break;
      applications.add(applicationRow(factory.application(customerId)));
      flushWhenFull(applications, "insert into applications(customer_id,product_code,channel,status,submitted_at,decisioned_at,funded_at) values (?,?,?,?,?,?,?)");
    }
    flush(applications, "insert into applications(customer_id,product_code,channel,status,submitted_at,decisioned_at,funded_at) values (?,?,?,?,?,?,?)");

    List<Object[]> accounts = new ArrayList<>(properties.batchSize());
    for (long customerId : customerIds) {
      for (int ordinal = 0; ordinal < factory.accountCount(customerId); ordinal++) {
        accounts.add(accountRow(factory.account(customerId, ordinal)));
        flushWhenFull(accounts, "insert into accounts(customer_id,product_code,status,balance,opened_at) values (?,?,?,?,?)");
      }
    }
    flush(accounts, "insert into accounts(customer_id,product_code,status,balance,opened_at) values (?,?,?,?,?)");
    jdbc.update("""
        update accounts a set application_id=(select ap.id from applications ap
          where ap.customer_id=a.customer_id and ap.product_code=a.product_code and ap.status='FUNDED'
          order by ap.id limit 1)
        where a.application_id is null and exists (select 1 from applications ap
          where ap.customer_id=a.customer_id and ap.product_code=a.product_code and ap.status='FUNDED')
        """);

    List<Long> accountIds = jdbc.queryForList("select id from accounts order by id", Long.class);
    List<Object[]> transactions = new ArrayList<>(properties.batchSize());
    for (long accountId : accountIds) {
      for (int ordinal = 0; ordinal < factory.transactionCount(accountId); ordinal++) {
        transactions.add(transactionRow(factory.transaction(accountId, ordinal)));
        flushWhenFull(transactions, "insert into transactions(account_id,amount,transaction_type,occurred_at,fraud_flag) values (?,?,?,?,?)");
      }
    }
    flush(transactions, "insert into transactions(account_id,amount,transaction_type,occurred_at,fraud_flag) values (?,?,?,?,?)");
    seedSnapshotsAndFraud(accountIds);
    log.info("Synthetic seed complete: {} customers, {} accounts and bounded analytical facts", customerIds.size(), accountIds.size());
  }

  private void seedReferenceData() {
    jdbc.update("""
        insert into products(product_code,display_name,product_family,customer_type,active) values
        ('BUSINESS_CHECKING','Business Checking','CHECKING','BUSINESS',true),
        ('CONSUMER_CHECKING','Consumer Checking','CHECKING','CONSUMER',true),
        ('BUSINESS_SAVINGS','Business Savings','SAVINGS','BUSINESS',true),
        ('CONSUMER_SAVINGS','Consumer Savings','SAVINGS','CONSUMER',true)
        on conflict (product_code) do nothing
        """);
    jdbc.update("""
        insert into rate_history(product_code,effective_at,annual_rate,offer_code,benchmark_rate)
        select product_code, now() - interval '30 days',
          case when product_family='SAVINGS' then 0.0372 else 0.0010 end, 'STANDARD', 0.0525
        from products on conflict do nothing
        """);
    jdbc.update("""
        insert into metric_definitions(metric_code,formula_version,owner,certification_status,effective_at,lineage) values
        ('TOTAL_DEPOSITS','demo-v1','Portfolio Analytics','DEMO',now(),'daily_account_snapshots.ledger_balance'),
        ('APPLICATIONS','demo-v1','Growth Analytics','DEMO',now(),'applications.id'),
        ('FRAUD_LOSS_RATE','demo-v1','Fraud Risk','DEMO',now(),'fraud_events.confirmed_loss / transactions.amount')
        on conflict (metric_code) do nothing
        """);
  }

  private void seedSnapshotsAndFraud(List<Long> accountIds) {
    jdbc.update("""
        insert into daily_account_snapshots(account_id,snapshot_date,ledger_balance,average_balance,
          transaction_count,inflow_amount,outflow_amount,churn_risk_flag,source_watermark,calculated_at)
        select a.id,current_date,a.balance,a.balance,count(t.id),
          coalesce(sum(t.amount) filter (where t.transaction_type='DEPOSIT'),0),
          coalesce(sum(t.amount) filter (where t.transaction_type<>'DEPOSIT'),0),c.risk_score < 560,now(),now()
        from accounts a join customers c on c.id=a.customer_id
        left join transactions t on t.account_id=a.id
        group by a.id,c.risk_score
        on conflict do nothing
        """);
    jdbc.update("""
        insert into fraud_events(transaction_id,transaction_occurred_at,event_type,status,suspected_amount,confirmed_loss,detected_at)
        select id,occurred_at,'PAYMENT_FRAUD','CONFIRMED',amount,round(amount * 0.70,2),occurred_at + interval '1 hour'
        from transactions where fraud_flag on conflict do nothing
        """);
    jdbc.update("""
        insert into daily_product_metrics(metric_code,product_code,metric_date,metric_value,formula_version,source_watermark,calculated_at)
        select 'TOTAL_DEPOSITS',a.product_code,current_date,sum(s.ledger_balance),'demo-v1',now(),now()
        from daily_account_snapshots s join accounts a on a.id=s.account_id group by a.product_code
        on conflict do nothing
        """);
  }

  private void batchRange(int start, int end, RowFactory rowFactory, String sql) {
    List<Object[]> rows = new ArrayList<>(properties.batchSize());
    for (int ordinal = start; ordinal <= end; ordinal++) {
      rows.add(rowFactory.create(ordinal));
      flushWhenFull(rows, sql);
    }
    flush(rows, sql);
  }

  private Object[] customerRow(Customer customer) {
    return new Object[] {customer.customerNumber(), customer.segment().name(), customer.region(), customer.riskScore(), customer.createdAt()};
  }

  private Object[] accountRow(DepositAccount account) {
    return new Object[] {account.customerId(), account.productCode().name(), account.status().name(),
        account.balance().amount(), account.openedAt()};
  }

  private Object[] transactionRow(BankingTransaction transaction) {
    return new Object[] {transaction.accountId(), transaction.amount().amount(), transaction.type().name(),
        Timestamp.from(transaction.occurredAt()), transaction.fraudFlag()};
  }

  private Object[] applicationRow(ProductApplication application) {
    return new Object[] {application.customerId(), application.productCode().name(), application.channel().name(),
        application.status().name(), Timestamp.from(application.submittedAt()), Timestamp.from(application.decisionedAt()),
        application.fundedAt() == null ? null : Timestamp.from(application.fundedAt())};
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
