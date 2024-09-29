package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // Cho phép tất cả các yêu cầu mà không cần xác thực
            )
            .csrf(csrf -> csrf.disable()); // Tắt bảo vệ CSRF (chỉ nên tắt trong quá trình phát triển)
        return http.build();
    }
}