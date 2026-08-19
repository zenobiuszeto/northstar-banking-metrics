package com.northstar.metrics.domain;

import java.util.Arrays;

public enum PortfolioScope {
  ALL("All products"), BUSINESS_CHECKING("Business Checking"), CONSUMER_CHECKING("Consumer Checking"),
  BUSINESS_SAVINGS("Business Savings"), CONSUMER_SAVINGS("Consumer Savings");
  private final String displayName;
  PortfolioScope(String displayName) { this.displayName = displayName; }
  public String displayName() { return displayName; }
  public static PortfolioScope fromDisplayName(String value) {
    return Arrays.stream(values()).filter(scope -> scope.displayName.equalsIgnoreCase(value.strip()))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("Unsupported product: " + value));
  }
}
