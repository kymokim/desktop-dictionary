package com.kymokim.desktopdictionary.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {
    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:change-me}")
    private String adminPassword;

    @Bean
    public String getAdminEmail() {
        return adminEmail;
    }
    @Bean
    public String getAdminPassword() {
        return adminPassword;
    }
}
