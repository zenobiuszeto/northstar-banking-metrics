#!/usr/bin/env bash
set -euo pipefail

: "${SOURCE_DATABASE_URL:?Set SOURCE_DATABASE_URL to a read-only PostgreSQL connection URL}"
: "${TARGET_DATABASE_URL:?Set TARGET_DATABASE_URL to the empty TimescaleDB connection URL}"

if [[ "$SOURCE_DATABASE_URL" == "$TARGET_DATABASE_URL" ]]; then
  echo "Source and target must be different databases." >&2
  exit 1
fi

for command_name in psql mktemp; do
  command -v "$command_name" >/dev/null || { echo "Missing required command: $command_name" >&2; exit 1; }
done

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
migration_tmp="$(mktemp -d "${TMPDIR:-/tmp}/northstar-migration.XXXXXX")"
trap 'rm -rf "$migration_tmp"' EXIT

target_customer_count="$(psql "$TARGET_DATABASE_URL" -XAtc "select count(*) from customers" 2>/dev/null || echo 0)"
if [[ "$target_customer_count" != "0" ]]; then
  echo "Target customers table is not empty; migration stopped to prevent duplicate data." >&2
  exit 1
fi

psql "$TARGET_DATABASE_URL" -v ON_ERROR_STOP=1 -f "$project_root/server/src/main/resources/schema.sql"

psql "$SOURCE_DATABASE_URL" -v ON_ERROR_STOP=1 -c "\copy (select id,customer_number,segment,region,risk_score,created_at from customers order by id) to '$migration_tmp/customers.csv' with (format csv, header true)"
psql "$SOURCE_DATABASE_URL" -v ON_ERROR_STOP=1 -c "\copy (select id,customer_id,product,status,balance,opened_at from accounts order by id) to '$migration_tmp/accounts.csv' with (format csv, header true)"
psql "$SOURCE_DATABASE_URL" -v ON_ERROR_STOP=1 -c "\copy (select id,account_id,amount,transaction_type,occurred_at,fraud_flag from transactions order by occurred_at,id) to '$migration_tmp/transactions.csv' with (format csv, header true)"

psql "$TARGET_DATABASE_URL" -v ON_ERROR_STOP=1 -c "\copy customers(id,customer_number,segment,region,risk_score,created_at) from '$migration_tmp/customers.csv' with (format csv, header true)"
psql "$TARGET_DATABASE_URL" -v ON_ERROR_STOP=1 -c "\copy accounts(id,customer_id,product,status,balance,opened_at) from '$migration_tmp/accounts.csv' with (format csv, header true)"
psql "$TARGET_DATABASE_URL" -v ON_ERROR_STOP=1 -c "\copy transactions(id,account_id,amount,transaction_type,occurred_at,fraud_flag) from '$migration_tmp/transactions.csv' with (format csv, header true)"

psql "$TARGET_DATABASE_URL" -v ON_ERROR_STOP=1 <<'SQL'
select setval(pg_get_serial_sequence('customers','id'), coalesce(max(id),1), max(id) is not null) from customers;
select setval(pg_get_serial_sequence('accounts','id'), coalesce(max(id),1), max(id) is not null) from accounts;
select setval(pg_get_serial_sequence('transactions','id'), coalesce(max(id),1), max(id) is not null) from transactions;
analyze customers;
analyze accounts;
analyze transactions;
SQL

for table_name in customers accounts transactions; do
  source_count="$(psql "$SOURCE_DATABASE_URL" -XAtc "select count(*) from $table_name")"
  target_count="$(psql "$TARGET_DATABASE_URL" -XAtc "select count(*) from $table_name")"
  if [[ "$source_count" != "$target_count" ]]; then
    echo "Validation failed for $table_name: source=$source_count target=$target_count" >&2
    exit 1
  fi
  echo "$table_name validated: $target_count rows"
done

echo "Bulk migration and row-count validation completed. Run financial aggregate reconciliation before cutover."
