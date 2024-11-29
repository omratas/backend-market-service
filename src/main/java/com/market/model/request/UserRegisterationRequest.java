package com.market.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterationRequest {
  private String name;
  private String surname;
  private String email;
  private String password;
  private String confirmPassword;

}
