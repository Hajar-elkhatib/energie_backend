package com.energie.platform.config;
import org.springframework.context.annotation.*; import org.springframework.web.cors.*; import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
@Configuration public class CorsConfig {@Bean CorsConfigurationSource corsConfigurationSource(){CorsConfiguration c=new CorsConfiguration();c.addAllowedOriginPattern("*");c.addAllowedHeader("*");c.addAllowedMethod("*");UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}}
