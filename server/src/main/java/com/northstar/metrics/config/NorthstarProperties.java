package com.northstar.metrics.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("northstar")
public record NorthstarProperties(@Valid Seed seed, @Valid Cors cors) {
  public record Seed(boolean enabled, @Min(1) @Max(1_000_000) int customerCount,
                     @Min(100) @Max(10_000) int batchSize) {}

  public record Cors(@NotEmpty List<String> allowedOrigins) {}
}
