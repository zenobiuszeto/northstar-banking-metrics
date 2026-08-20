package com.northstar.metrics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BankingDomainTest {
  @Test void rejectsOutOfRangeCustomerRiskScores() {
    assertThatThrownBy(() -> new Customer(null, "CUS-1", Customer.Segment.CONSUMER, "West", 851, LocalDate.now()))
        .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Risk score");
  }

  @Test void closingAnAccountRequiresAValidDate() {
    DepositAccount account = new DepositAccount(1L, 2L, Product.Code.CONSUMER_SAVINGS,
        DepositAccount.Status.OPEN, Money.usd(new BigDecimal("100.00")), LocalDate.of(2026, 1, 1), null);
    assertThat(account.close(LocalDate.of(2026, 2, 1)).status()).isEqualTo(DepositAccount.Status.CLOSED);
    assertThatThrownBy(() -> account.close(LocalDate.of(2025, 12, 31))).isInstanceOf(IllegalArgumentException.class);
  }

  @Test void enforcesApplicationStateTimestamps() {
    assertThatThrownBy(() -> new ProductApplication(null, 1, Product.Code.BUSINESS_CHECKING,
        ProductApplication.Channel.DIGITAL, ProductApplication.Status.FUNDED, Instant.now(), Instant.now(), null))
        .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("funding timestamp");
  }

  @Test void confirmedFraudLossCannotExceedExposure() {
    assertThatThrownBy(() -> new FraudEvent(null, null, 1L, FraudEvent.Type.PAYMENT_FRAUD,
        FraudEvent.Status.CONFIRMED, Money.usd(BigDecimal.TEN), Money.usd(new BigDecimal("11")), Instant.now()))
        .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Confirmed loss");
  }

  @Test void internalTransfersAreExcludedFromExternalFlow() {
    BankingTransaction transaction = new BankingTransaction(1L, 1, Money.usd(BigDecimal.ONE),
        BankingTransaction.Type.INTERNAL_TRANSFER, Instant.now(), false);
    assertThat(transaction.isExternalFlow()).isFalse();
  }
}
