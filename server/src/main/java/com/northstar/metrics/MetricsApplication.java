package com.northstar.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
public class MetricsApplication {
  public static void main(String[] args) { SpringApplication.run(MetricsApplication.class, args); }

  @RestController @RequestMapping("/api/metrics") @CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
  static class MetricsController {
    @GetMapping
    Map<String, Object> overview(@RequestParam(defaultValue = "All products") String product) {
      Map<String, double[]> values = Map.of(
        "All products", new double[]{18462, 71.8, 486.2, .42, 1.9, 86.4, 1246},
        "Business Checking", new double[]{4218, 74.2, 184.6, .31, 1.1, 91.2, 3210},
        "Consumer Checking", new double[]{6774, 70.3, 126.4, .49, 2.4, 83.8, 880},
        "Business Savings", new double[]{2391, 76.1, 87.5, .27, .9, 93.5, 2890},
        "Consumer Savings", new double[]{5079, 69.7, 87.7, .53, 2.1, 85.4, 730});
      double[] v = values.getOrDefault(product, values.get("All products"));
      return Map.ofEntries(Map.entry("product", product), Map.entry("customerCount", 100000),
        Map.entry("applications", (int)v[0]), Map.entry("approvalRate", v[1]), Map.entry("depositsMillions", v[2]),
        Map.entry("fraudRate", v[3]), Map.entry("monthlyChurnRate", v[4]), Map.entry("retentionRate", v[5]),
        Map.entry("lifetimeValue", v[6]), Map.entry("atRiskCustomers", 4218),
        Map.entry("depositsAtRiskMillions", 9.6), Map.entry("winBackRate", 42.1));
    }
  }
}
