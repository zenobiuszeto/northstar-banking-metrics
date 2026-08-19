package com.northstar.metrics;

import com.northstar.metrics.config.NorthstarProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(NorthstarProperties.class)
public class MetricsApplication {
  public static void main(String[] args) {
    SpringApplication.run(MetricsApplication.class, args);
  }
}
