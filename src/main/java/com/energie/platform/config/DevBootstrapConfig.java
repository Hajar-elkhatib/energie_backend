package com.energie.platform.config;

import com.energie.platform.model.Administrateur;
import com.energie.platform.repository.Repositories.AdministrateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Development-only bootstrap: converts the SQL marker into a fresh BCrypt password once. */
@Configuration @Profile("dev") @RequiredArgsConstructor
public class DevBootstrapConfig {
  private static final String SQL_MARKER = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
  private final AdministrateurRepository admins;
  private final PasswordEncoder encoder;
  @Bean CommandLineRunner bootstrapAdmin() {
    return args -> {
      Administrateur admin = admins.findByLogin("admin").orElseGet(() -> { Administrateur a = new Administrateur(); a.setLogin("admin"); return a; });
      if (admin.getId() == null || SQL_MARKER.equals(admin.getMotDePasse())) { admin.setMotDePasse(encoder.encode("password")); admins.save(admin); }
    };
  }
}
