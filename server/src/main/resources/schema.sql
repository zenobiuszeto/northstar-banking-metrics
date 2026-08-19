create extension if not exists timescaledb;
create table if not exists customers (
  id bigserial primary key, customer_number varchar(24) unique not null, segment varchar(24) not null,
  region varchar(24) not null, risk_score integer not null, created_at date not null
);
create table if not exists accounts (
  id bigserial primary key, customer_id bigint not null references customers(id), product varchar(32) not null,
  status varchar(16) not null, balance numeric(16,2) not null, opened_at date not null
);
create table if not exists transactions (
  id bigserial not null, account_id bigint not null references accounts(id), amount numeric(16,2) not null,
  transaction_type varchar(20) not null, occurred_at timestamp not null, fraud_flag boolean not null,
  primary key (id, occurred_at)
);
select create_hypertable('transactions', by_range('occurred_at'), if_not_exists => true);
create index if not exists idx_accounts_customer on accounts(customer_id);
create index if not exists idx_transactions_account on transactions(account_id);
