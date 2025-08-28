package com.example.crudapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);
    
    @Value("${app.allowed-origin:}")
    private String allowedOrigin;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (allowedOrigin == null || allowedOrigin.trim().isEmpty()) {
            logger.info("🔓 CORS is open (no ALLOWED_ORIGIN set)");
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "DELETE", "OPTIONS")
                    .allowedHeaders("Content-Type", "Authorization")
                    .allowCredentials(true);
        } else {
            logger.info("✅ CORS enabled for: {}", allowedOrigin);
            registry.addMapping("/**")
                    .allowedOrigins(allowedOrigin)
                    .allowedMethods("GET", "POST", "DELETE", "OPTIONS")
                    .allowedHeaders("Content-Type", "Authorization")
                    .allowCredentials(true);
        }
    }
}