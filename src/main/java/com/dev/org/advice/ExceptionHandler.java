package com.dev.org.advice;

import com.dev.org.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for the application. Handles unchecked exceptions and
 * Spring validation exceptions. Converts exceptions to RFC 7807 Problem Details with
 * consistent structure. Includes centralized logging and trace ID propagation.
 */
@RestControllerAdvice
public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * Handles custom unchecked ApplicationException.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(
            ApplicationException ex, HttpServletRequest request) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail(ex.getMessage());

        problemDetail.setProperty(TRACE_ID_KEY, MDC.get(TRACE_ID_KEY));
        problemDetail.setProperty("path", request.getRequestURI());

        log.error("Application exception", ex);
        return problemDetail;
    }

    /**
     * Handles Spring's @Valid validation failures (400 Bad Request).
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            fieldErrors.put(fieldName, errorMessage);
                        });

        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("Validation failed");
        problemDetail.setProperty("fieldErrors", fieldErrors);

        problemDetail.setProperty(TRACE_ID_KEY, MDC.get(TRACE_ID_KEY));
        problemDetail.setProperty("path", request.getRequestURI());

        log.error("Validation failed");

        return problemDetail;
    }

    /**
     * Handles type mismatch in request parameters (400 Bad Request).
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(
            MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : "unknown";
        String message =
                String.format(
                        "Invalid value '%s' for parameter '%s'. Expected type: %s",
                        ex.getValue(), ex.getName(), expectedType);

        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail(message);

        problemDetail.setProperty(TRACE_ID_KEY, MDC.get(TRACE_ID_KEY));
        problemDetail.setProperty("path", request.getRequestURI());

        log.error("Type mismatch");

        return problemDetail;
    }

    /**
     * Handles all other uncaught exceptions (500 Internal Server Error).
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setDetail("An unexpected error occurred. Please try again later.");

        problemDetail.setProperty(TRACE_ID_KEY, MDC.get(TRACE_ID_KEY));
        problemDetail.setProperty("path", request.getRequestURI());

        log.error("Unhandled exception", ex);

        return problemDetail;
    }
}
