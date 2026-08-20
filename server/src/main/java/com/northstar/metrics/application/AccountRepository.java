package com.northstar.metrics.application;

import com.northstar.metrics.domain.DepositAccount;
import java.util.Optional;

public interface AccountRepository {
  Optional<DepositAccount> findById(long accountId);
}
