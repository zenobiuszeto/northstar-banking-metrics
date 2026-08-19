create extension if not exists timescaledb;

create table if not exists customers (
  id bigserial primary key,
  customer_number varchar(24) unique not null,
  segment varchar(24) not null check (segment in ('BUSINESS','CONSUMER')),
  region varchar(24) not null,
  risk_score integer not null check (risk_score between 300 and 850),
  created_at date not null
);

create table if not exists products (
  product_code varchar(32) primary key,
  display_name varchar(64) unique not null,
  product_family varchar(16) not null check (product_family in ('CHECKING','SAVINGS')),
  customer_type varchar(16) not null check (customer_type in ('BUSINESS','CONSUMER')),
  active boolean not null default true
);

create table if not exists applications (
  id bigserial primary key,
  customer_id bigint not null references customers(id),
  product_code varchar(32) not null references products(product_code),
  channel varchar(16) not null check (channel in ('BRANCH','DIGITAL','PARTNER')),
  status varchar(16) not null check (status in ('SUBMITTED','APPROVED','DECLINED','FUNDED')),
  submitted_at timestamptz not null,
  decisioned_at timestamptz,
  funded_at timestamptz,
  check (decisioned_at is null or decisioned_at >= submitted_at),
  check (funded_at is null or (decisioned_at is not null and funded_at >= decisioned_at)),
  check (status <> 'FUNDED' or funded_at is not null)
);

create table if not exists accounts (
  id bigserial primary key,
  customer_id bigint not null references customers(id),
  application_id bigint references applications(id),
  product_code varchar(32) not null references products(product_code),
  status varchar(16) not null check (status in ('OPEN','DORMANT','CLOSED')),
  balance numeric(16,2) not null check (balance >= 0),
  opened_at date not null,
  closed_at date,
  check (closed_at is null or closed_at >= opened_at),
  check (status <> 'CLOSED' or closed_at is not null)
);

create table if not exists transactions (
  id bigserial not null,
  account_id bigint not null references accounts(id),
  amount numeric(16,2) not null check (amount > 0),
  currency char(3) not null default 'USD',
  transaction_type varchar(24) not null check (transaction_type in ('DEPOSIT','PAYMENT','WITHDRAWAL','INTERNAL_TRANSFER')),
  occurred_at timestamptz not null,
  fraud_flag boolean not null,
  primary key (id, occurred_at)
);
select create_hypertable('transactions', by_range('occurred_at'), if_not_exists => true);

create table if not exists fraud_events (
  id bigserial primary key,
  application_id bigint references applications(id),
  transaction_id bigint,
  transaction_occurred_at timestamptz,
  event_type varchar(24) not null check (event_type in ('APPLICATION_FRAUD','PAYMENT_FRAUD','ACCOUNT_TAKEOVER')),
  status varchar(16) not null check (status in ('OPEN','CONFIRMED','DISMISSED','RECOVERED')),
  suspected_amount numeric(16,2) not null check (suspected_amount >= 0),
  confirmed_loss numeric(16,2) not null check (confirmed_loss >= 0 and confirmed_loss <= suspected_amount),
  detected_at timestamptz not null,
  check (application_id is not null or transaction_id is not null),
  foreign key (transaction_id, transaction_occurred_at) references transactions(id, occurred_at)
);

create table if not exists rate_history (
  product_code varchar(32) not null references products(product_code),
  effective_at timestamptz not null,
  annual_rate numeric(9,6) not null check (annual_rate >= 0),
  offer_code varchar(32) not null default 'STANDARD',
  benchmark_rate numeric(9,6) not null check (benchmark_rate >= 0),
  primary key (product_code, effective_at)
);
select create_hypertable('rate_history', by_range('effective_at'), if_not_exists => true);

create table if not exists daily_account_snapshots (
  account_id bigint not null references accounts(id),
  snapshot_date date not null,
  ledger_balance numeric(16,2) not null check (ledger_balance >= 0),
  average_balance numeric(16,2) not null check (average_balance >= 0),
  transaction_count integer not null check (transaction_count >= 0),
  inflow_amount numeric(16,2) not null check (inflow_amount >= 0),
  outflow_amount numeric(16,2) not null check (outflow_amount >= 0),
  churn_risk_flag boolean not null,
  source_watermark timestamptz not null,
  calculated_at timestamptz not null,
  primary key (account_id, snapshot_date)
);

create table if not exists metric_definitions (
  metric_code varchar(48) primary key,
  formula_version varchar(24) not null,
  owner varchar(64) not null,
  certification_status varchar(16) not null check (certification_status in ('DEMO','DRAFT','CERTIFIED')),
  effective_at timestamptz not null,
  lineage text not null
);

create table if not exists daily_product_metrics (
  metric_code varchar(48) not null references metric_definitions(metric_code),
  product_code varchar(32) not null references products(product_code),
  metric_date date not null,
  metric_value numeric(20,6) not null,
  formula_version varchar(24) not null,
  source_watermark timestamptz not null,
  calculated_at timestamptz not null,
  primary key (metric_code, product_code, metric_date)
);

create index if not exists idx_accounts_customer on accounts(customer_id);
create index if not exists idx_applications_customer on applications(customer_id);
create index if not exists idx_transactions_account_time on transactions(account_id, occurred_at desc);
create index if not exists idx_fraud_events_detected on fraud_events(detected_at desc);
create index if not exists idx_snapshots_date on daily_account_snapshots(snapshot_date desc);
create index if not exists idx_product_metrics_date on daily_product_metrics(metric_date desc);
