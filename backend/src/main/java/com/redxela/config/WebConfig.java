package com.redxela.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * Lets the interface, served from another origin (the Vite dev server or the
 * deployed site), call the REST API. The allowed origins come from
 * redxela.cors.allowed-origins, a comma-separated list.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${redxela.cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT");
    }
}