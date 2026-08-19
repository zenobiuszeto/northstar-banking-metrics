package com.northstar.metrics.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
  public Money {
    Objects.requireNonNull(amount); Objects.requireNonNull(currency);
    if (currency.length() != 3) throw new IllegalArgumentException("Currency must be a three-letter code");
    amount = amount.setScale(2, RoundingMode.HALF_UP);
  }
  public static Money usd(BigDecimal amount) { return new Money(amount, "USD"); }
  public boolean isNegative() { return amount.signum() < 0; }
}
