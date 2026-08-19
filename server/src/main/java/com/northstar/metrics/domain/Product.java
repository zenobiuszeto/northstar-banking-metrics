package com.northstar.metrics.domain;

import java.util.Objects;

public record Product(Code code, Family family, CustomerType customerType, String displayName, boolean active) {
  public Product {
    Objects.requireNonNull(code); Objects.requireNonNull(family); Objects.requireNonNull(customerType);
    if (displayName == null || displayName.isBlank()) throw new IllegalArgumentException("Product display name is required");
  }
  public enum Code { BUSINESS_CHECKING, CONSUMER_CHECKING, BUSINESS_SAVINGS, CONSUMER_SAVINGS }
  public enum Family { CHECKING, SAVINGS }
  public enum CustomerType { BUSINESS, CONSUMER }
}
