package com.northstar.metrics.infrastructure.metrics;

import com.northstar.metrics.application.AccountRepository;
import com.northstar.metrics.domain.DepositAccount;
import com.northstar.metrics.domain.Money;
import com.northstar.metrics.domain.Product;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcAccountRepository implements AccountRepository {
  private final JdbcClient jdbc;
  JdbcAccountRepository(JdbcClient jdbc) { this.jdbc = jdbc; }
  @Override public Optional<DepositAccount> findById(long accountId) {
    try {
      return Optional.ofNullable(jdbc.sql("select * from accounts where id=:id").param("id", accountId)
          .query((rs, row) -> new DepositAccount(rs.getLong("id"), rs.getLong("customer_id"),
              Product.Code.valueOf(rs.getString("product_code")), DepositAccount.Status.valueOf(rs.getString("status")),
              Money.usd(rs.getBigDecimal("balance")), rs.getDate("opened_at").toLocalDate(),
              rs.getDate("closed_at") == null ? null : rs.getDate("closed_at").toLocalDate())).single());
    } catch (EmptyResultDataAccessException exception) { return Optional.empty(); }
  }
}
