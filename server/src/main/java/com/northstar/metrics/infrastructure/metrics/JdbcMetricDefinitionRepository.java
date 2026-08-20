package com.northstar.metrics.infrastructure.metrics;

import com.northstar.metrics.application.MetricDefinitionRepository;
import com.northstar.metrics.domain.GovernedMetric;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcMetricDefinitionRepository implements MetricDefinitionRepository {
  private final JdbcClient jdbc;
  JdbcMetricDefinitionRepository(JdbcClient jdbc) { this.jdbc = jdbc; }
  @Override public Optional<GovernedMetric.Definition> findByCode(String metricCode) {
    try {
      return Optional.ofNullable(jdbc.sql("select * from metric_definitions where metric_code=:code")
          .param("code", metricCode).query((rs, row) -> new GovernedMetric.Definition(
              rs.getString("metric_code"), rs.getString("formula_version"), rs.getString("owner"),
              GovernedMetric.Certification.valueOf(rs.getString("certification_status")),
              rs.getTimestamp("effective_at").toInstant(), rs.getString("lineage"))).single());
    } catch (EmptyResultDataAccessException exception) { return Optional.empty(); }
  }
}
