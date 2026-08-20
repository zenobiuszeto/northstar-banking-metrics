package com.northstar.metrics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductTest {
  @Test
  void resolvesDashboardScopesWithoutCaseSensitivity() {
    assertThat(PortfolioScope.fromDisplayName("business checking")).isEqualTo(PortfolioScope.BUSINESS_CHECKING);
  }

  @Test
  void rejectsUnknownProducts() {
    assertThatThrownBy(() -> PortfolioScope.fromDisplayName("Credit Card"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported product");
  }
}
