package com.market.security;

import com.market.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {


  private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

  // Extract username from token
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Extract specific claim from token
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // Extract all claims
  private Claims extractAllClaims(String token) {
    String secret = "your-very-long-secret-key-which-must-be-at-least-32-bytes-long";
    String encodedKey = Base64.getEncoder().encodeToString(secret.getBytes());
    return Jwts.parserBuilder()
        .setSigningKey(encodedKey.getBytes(StandardCharsets.UTF_8))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // Check if token is expired
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // Extract expiration date from token
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // Validate token
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  // Generate a token
  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, user.getUsername());
  }

  // Helper method to create a token
  private String createToken(Map<String, Object> claims, String subject) {
    String secret = "your-very-long-secret-key-which-must-be-at-least-32-bytes-long";
    String encodedKey = Base64.getEncoder().encodeToString(secret.getBytes());
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS256, encodedKey.getBytes(StandardCharsets.UTF_8))
        .compact();
  }
}