package com.market.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

  private String email;
  private String password;
}

