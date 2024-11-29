package com.market.controller;

import com.market.model.User;
import com.market.model.request.AuthRequest;
import com.market.model.response.AuthResponse;
import com.market.repository.UserRepository;
import com.market.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
    );

    User user = userRepository.findByUsername(authRequest.getUsername());
    String token = jwtUtil.generateToken(user);

    return ResponseEntity.ok(new AuthResponse(token));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody User user) {
    if (userRepository.findByUsername(user.getUsername()) != null) {
      throw new IllegalStateException("Username already exists");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setEnabled(true);
    userRepository.save(user);

    return ResponseEntity.ok("User registered successfully");
  }
}