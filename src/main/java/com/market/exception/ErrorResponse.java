package com.market.exception;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private String message;
  private String errorCode;
  private LocalDateTime timestamp;
  private String details;
  private String path;
  private int status;

  public ErrorResponse(String message, String errorCode, LocalDateTime timestamp, String details, String path, int status) {
    this.message = message;
    this.errorCode = errorCode;
    this.timestamp = timestamp;
    this.details = details;
    this.path = path;
    this.status = status;
  }
}
