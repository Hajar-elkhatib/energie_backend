package com.energie.platform.config;

import com.energie.platform.model.Administrateur;
import com.energie.platform.repository.Repositories.AdministrateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration 
@RequiredArgsConstructor
public class DevBootstrapConfig {
  private final AdministrateurRepository admins;
  private final PasswordEncoder encoder;

  @Bean 
  CommandLineRunner bootstrapAdmin() {
    return args -> {
      Administrateur admin = admins.findByLogin("admin").orElseGet(() -> { 
        Administrateur a = new Administrateur(); 
        a.setLogin("admin"); 
        return a; 
      });
      admin.setMotDePasse(encoder.encode("admin123"));
      admins.save(admin);
      System.out.println(">>> Compte administrateur initialisé avec succès : login=admin, password=admin123");
    };
  }
}
