package com.northstar.metrics.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.northstar.metrics.application.MetricsQueryService;
import com.northstar.metrics.domain.PortfolioMetrics;
import com.northstar.metrics.domain.PortfolioScope;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MetricsControllerTest {
  private MockMvc mvc;
  private MetricsQueryService service;

  @BeforeEach
  void setUp() {
    service = org.mockito.Mockito.mock(MetricsQueryService.class);
    MetricsController controller = new MetricsController(service,
        new MetricsResponseMapper(Clock.fixed(Instant.parse("2026-08-19T12:00:00Z"), ZoneOffset.UTC)));
    mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ApiExceptionHandler()).build();
  }

  @Test
  void returnsProblemDetailsForInvalidProducts() throws Exception {
    when(service.overview(any())).thenThrow(new IllegalArgumentException("Unsupported product: Other"));
    mvc.perform(get("/api/v1/metrics").param("product", "Other"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Invalid request"));
  }

  @Test
  void mapsDomainMetricsOnTheVersionedEndpoint() throws Exception {
    when(service.overview("All products")).thenReturn(new PortfolioMetrics(PortfolioScope.ALL, 100_000,
        18_462, new BigDecimal("71.8"), new BigDecimal("486.2"), new BigDecimal("0.42"),
        new BigDecimal("1.9"), new BigDecimal("86.4"), new BigDecimal("1246"), 4_218,
        new BigDecimal("9.6"), new BigDecimal("42.1")));
    mvc.perform(get("/api/v1/metrics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.product").value("All products"))
        .andExpect(jsonPath("$.customerCount").value(100000))
        .andExpect(jsonPath("$.generatedAt").value("2026-08-19T12:00:00Z"));
  }
}
