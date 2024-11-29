package com.market.controller;

import com.market.exception.CustomException;
import com.market.model.User;
import com.market.model.request.AuthRequest;
import com.market.model.request.UserRegisterationRequest;
import com.market.model.response.AuthResponse;
import com.market.security.JwtUtil;
import com.market.service.UserService;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final MessageSource messageSource;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
            authRequest.getPassword())
    );

    final Optional<User> optionalUser = userService.getUserByEmail(authRequest.getEmail());
    if (optionalUser.isEmpty()){
      throw new CustomException(
          messageSource.getMessage("error.user-not-found",
              new Object[]{authRequest.getEmail()},
              Locale.US), "10001");
    }
    final User user = optionalUser.get();
    String token = jwtUtil.generateToken(user);

    return ResponseEntity.ok(new AuthResponse(token));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserRegisterationRequest registerationRequest) {
    final Optional<User> optionalUser = userService.getUserByEmail(registerationRequest.getEmail());
    if (optionalUser.isPresent()) {
      throw new CustomException(
          messageSource.getMessage("error.user-already-exist",
              new Object[]{registerationRequest.getEmail()},
              Locale.US), "10002");
    }
    if (!registerationRequest.getPassword().equals(registerationRequest.getConfirmPassword())) {
      throw new CustomException(
          messageSource.getMessage("error.password-not-match",
              null,
              Locale.US), "10003");
    }
    User newUser = new User();
    newUser.setEmail(registerationRequest.getEmail());
    newUser.setName(registerationRequest.getName());
    newUser.setSurname(registerationRequest.getSurname());
    newUser.setPassword(passwordEncoder.encode(registerationRequest.getPassword()));
    newUser.setEnabled(true);
    userService.save(newUser);
    return ResponseEntity.ok("User registered successfully");
  }
}