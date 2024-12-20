package com.market.service;

import com.market.exception.CustomException;
import com.market.model.User;
import com.market.model.response.UserResponse;
import com.market.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final MessageSource messageSource;

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public void save(User user) {
    userRepository.save(user);
  }

  public List<UserResponse> getAllUser(){
    final List<User> users = userRepository.findAll();
    return users.stream().map(this::toUserResponse).toList();
  }

  public UserResponse toUserResponse(User user){
    return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .surname(user.getSurname())
        .email(user.getEmail()).build();
  }
}
