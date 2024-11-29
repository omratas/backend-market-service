package com.market.service;

import com.market.exception.CustomException;
import com.market.model.User;
import com.market.repository.UserRepository;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final MessageSource messageSource;


  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow((() -> new CustomException(
            messageSource.getMessage("error.user-not-found", new Object[]{email},
                Locale.US), "10001")));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .authorities(user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList()))
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(!user.isEnabled())
        .build();
  }
}