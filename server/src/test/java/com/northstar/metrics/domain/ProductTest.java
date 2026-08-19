package com.northstar.metrics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductTest {
  @Test
  void resolvesDisplayNamesWithoutCaseSensitivity() {
    assertThat(Product.fromDisplayName("business checking")).isEqualTo(Product.BUSINESS_CHECKING);
  }

  @Test
  void rejectsUnknownProducts() {
    assertThatThrownBy(() -> Product.fromDisplayName("Credit Card"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported product");
  }
}
