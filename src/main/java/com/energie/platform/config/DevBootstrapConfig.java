package com.energie.platform.config;

import com.energie.platform.model.Administrateur;
import com.energie.platform.repository.Repositories.AdministrateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevBootstrapConfig {
  private static final String SQL_BOOTSTRAP_MARKER = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
  private final AdministrateurRepository admins;
  private final PasswordEncoder encoder;

  @Bean 
  CommandLineRunner bootstrapAdmin() {
    return args -> {
      Administrateur admin = admins.findByLogin("admin").orElse(null);
      if (admin == null) {
        admin = new Administrateur();
        admin.setLogin("admin");
        admin.setMotDePasse(encoder.encode("admin123"));
        admins.save(admin);
        System.out.println(">>> Compte administrateur de développement créé : login=admin");
      } else if (SQL_BOOTSTRAP_MARKER.equals(admin.getMotDePasse())) {
        admin.setMotDePasse(encoder.encode("admin123"));
        admins.save(admin);
        System.out.println(">>> Compte administrateur de développement migré.");
      }
    };
  }
}
