package com.northstar.metrics.application;

import com.northstar.metrics.domain.BankingTransaction;
import java.time.Instant;
import java.util.List;

public interface TransactionRepository {
  List<BankingTransaction> findForAccountSince(long accountId, Instant since, int limit);
}
