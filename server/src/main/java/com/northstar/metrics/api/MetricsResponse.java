package com.northstar.metrics.api;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING) Instant generatedAt) {}
