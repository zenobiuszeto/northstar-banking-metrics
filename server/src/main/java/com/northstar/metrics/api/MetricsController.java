package com.northstar.metrics.api;

import com.northstar.metrics.application.MetricsQueryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
  private final MetricsQueryService service;
  private final MetricsResponseMapper mapper;

  public MetricsController(MetricsQueryService service, MetricsResponseMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping
  public MetricsResponse overview(
      @RequestParam(defaultValue = "All products") @NotBlank @Size(max = 64) String product) {
    return mapper.toResponse(service.overview(product));
  }
}
