package com.northstar.metrics.api;

import java.math.BigDecimal;
import java.time.Instant;

public record MetricsResponse(
    String product,
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
    BigDecimal winBackRate,
    Instant generatedAt) {}
