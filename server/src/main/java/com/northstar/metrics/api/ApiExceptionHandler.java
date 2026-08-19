package com.northstar.metrics.api;

import com.northstar.metrics.application.MetricsNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
  ProblemDetail badRequest(Exception exception) {
    return problem(HttpStatus.BAD_REQUEST, "Invalid request", exception.getMessage());
  }

  @ExceptionHandler(MetricsNotFoundException.class)
  ProblemDetail notFound(MetricsNotFoundException exception) {
    return problem(HttpStatus.NOT_FOUND, "Metrics not found", exception.getMessage());
  }

  private ProblemDetail problem(HttpStatus status, String title, String detail) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    problem.setType(URI.create("https://northstar.example/problems/" + status.value()));
    return problem;
  }
}
