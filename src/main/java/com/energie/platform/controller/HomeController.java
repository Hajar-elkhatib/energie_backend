package com.energie.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "status", "UP",
            "application", "Energie Platform Backend API",
            "version", "1.0.0",
            "swagger", "/swagger-ui.html"
        );
    }
}
