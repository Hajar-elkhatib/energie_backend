package com.energie.platform.service;

import com.energie.platform.dto.Dtos.*;
import com.energie.platform.exception.ApiException;
import com.energie.platform.repository.Repositories.AdministrateurRepository;
import com.energie.platform.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AdministrateurRepository admins;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public TokenResponse login(LoginRequest r) {
    var a = admins.findByLogin(r.login()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));
    
    if (!encoder.matches(r.motDePasse(), a.getMotDePasse())) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
    }

    String token = jwt.generate(a.getLogin());
    return new TokenResponse(token, "Bearer");
  }
}
