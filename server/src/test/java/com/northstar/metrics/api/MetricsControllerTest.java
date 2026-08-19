package com.northstar.metrics.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.northstar.metrics.application.MetricsQueryService;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MetricsControllerTest {
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    MetricsQueryService service = org.mockito.Mockito.mock(MetricsQueryService.class);
    when(service.overview(any())).thenThrow(new IllegalArgumentException("Unsupported product: Other"));
    MetricsController controller = new MetricsController(service, new MetricsResponseMapper(Clock.systemUTC()));
    mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ApiExceptionHandler()).build();
  }

  @Test
  void returnsProblemDetailsForInvalidProducts() throws Exception {
    mvc.perform(get("/api/metrics").param("product", "Other"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Invalid request"));
  }
}
