package com.northstar.metrics.infrastructure.metrics;

import com.northstar.metrics.application.TransactionRepository;
import com.northstar.metrics.domain.BankingTransaction;
import com.northstar.metrics.domain.Money;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcTransactionRepository implements TransactionRepository {
  private final JdbcClient jdbc;
  JdbcTransactionRepository(JdbcClient jdbc) { this.jdbc = jdbc; }
  @Override public List<BankingTransaction> findForAccountSince(long accountId, Instant since, int limit) {
    return jdbc.sql("""
        select id,account_id,amount,currency,transaction_type,occurred_at,fraud_flag from transactions
        where account_id=:accountId and occurred_at>=:since order by occurred_at desc limit :limit
        """).param("accountId", accountId).param("since", since).param("limit", limit)
        .query((rs, row) -> new BankingTransaction(rs.getLong("id"), rs.getLong("account_id"),
            new Money(rs.getBigDecimal("amount"), rs.getString("currency")),
            BankingTransaction.Type.valueOf(rs.getString("transaction_type")),
            rs.getTimestamp("occurred_at").toInstant(), rs.getBoolean("fraud_flag"))).list();
  }
}
