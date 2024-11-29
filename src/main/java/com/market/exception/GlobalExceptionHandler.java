package com.market.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(CustomException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, WebRequest request) {
    String traceId = UUID.randomUUID().toString();
    logger.error("Trace ID: {} - Custom exception occurred: {}", traceId, ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        ex.getMessage(),
        ex.getErrorCode(),
        LocalDateTime.now(),
        "Custom exception occurred in the application.",
        request.getDescription(false),
        HttpStatus.BAD_REQUEST.value()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Validation Error");
    response.put("message", "Input validation failed");
    response.put("path", request.getDescription(false));
    response.put("details", errors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    String traceId = UUID.randomUUID().toString();
    log.error("Trace ID: {} - Generic exception occurred: {}", traceId, ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        ex.getMessage(),
        "INTERNAL_SERVER_ERROR",
        LocalDateTime.now(),
        "An unexpected error occurred in the application.",
        request.getDescription(false),
        HttpStatus.INTERNAL_SERVER_ERROR.value()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}