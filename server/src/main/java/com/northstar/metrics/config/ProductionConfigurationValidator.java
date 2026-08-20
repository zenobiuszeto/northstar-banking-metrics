package com.northstar.metrics.config;

import java.util.Locale;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("production")
class ProductionConfigurationValidator implements ApplicationRunner {
  private final DataSourceProperties datasource;
  private final NorthstarProperties northstar;
  ProductionConfigurationValidator(DataSourceProperties datasource, NorthstarProperties northstar) {
    this.datasource = datasource; this.northstar = northstar;
  }
  @Override public void run(ApplicationArguments args) {
    String password = datasource.getPassword();
    if (password == null || password.isBlank() || password.equals("northstar"))
      throw new IllegalStateException("Production requires a non-default database credential");
    boolean localCors = northstar.cors().allowedOrigins().stream()
        .map(value -> value.toLowerCase(Locale.ROOT)).anyMatch(value -> value.contains("localhost") || value.contains("127.0.0.1"));
    if (localCors) throw new IllegalStateException("Production CORS origins must not include loopback hosts");
    if (northstar.seed().enabled()) throw new IllegalStateException("Synthetic seeding must be disabled in production");
  }
}
