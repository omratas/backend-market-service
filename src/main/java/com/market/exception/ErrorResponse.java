package com.market.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private int status;
  private String message;
  private String timestamp;
  private String path;

  public ErrorResponse(int status, String message, String timestamp, String path) {
    this.status = status;
    this.message = message;
    this.timestamp = timestamp;
    this.path = path;
  }

}
