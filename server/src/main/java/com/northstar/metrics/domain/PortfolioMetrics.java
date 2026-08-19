package com.northstar.metrics.domain;

import java.math.BigDecimal;

public record PortfolioMetrics(
    PortfolioScope scope,
    int customerCount,
    int applications,
    BigDecimal approvalRate,
    BigDecimal depositsMillions,
    BigDecimal fraudRate,
    BigDecimal monthlyChurnRate,
    BigDecimal retentionRate,
    BigDecimal lifetimeValue,
    int atRiskCustomers,
    BigDecimal depositsAtRiskMillions,
    BigDecimal winBackRate) {}
