package com.northstar.metrics.application;

import com.northstar.metrics.domain.BankingTransaction;
import com.northstar.metrics.domain.DepositAccount;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountActivityService {
  private final AccountRepository accounts;
  private final TransactionRepository transactions;
  private final Clock clock;

  public AccountActivityService(AccountRepository accounts, TransactionRepository transactions, Clock clock) {
    this.accounts = accounts; this.transactions = transactions; this.clock = clock;
  }

  public AccountActivity load(long accountId, Duration window, int limit) {
    if (window.isNegative() || window.isZero()) throw new IllegalArgumentException("Activity window must be positive");
    if (limit < 1 || limit > 1_000) throw new IllegalArgumentException("Transaction limit must be between 1 and 1000");
    DepositAccount account = accounts.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Unknown account: " + accountId));
    List<BankingTransaction> activity = transactions.findForAccountSince(accountId, clock.instant().minus(window), limit);
    return new AccountActivity(account, List.copyOf(activity));
  }

  public record AccountActivity(DepositAccount account, List<BankingTransaction> transactions) {}
}
