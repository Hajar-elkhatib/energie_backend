package com.energie.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.energie.platform.repository", considerNestedRepositories = true)
public class EnergiePlatformApplication {
  public static void main(String[] args) { SpringApplication.run(EnergiePlatformApplication.class, args); }
}
