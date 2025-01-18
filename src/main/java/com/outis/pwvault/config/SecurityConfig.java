package com.outis.pwvault.config;

import com.outis.pwvault.filter.IpValidationFilter;
import com.outis.pwvault.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final IpValidationFilter ipValidationFilter;
    private final JwtFilter jwtFilter;

    public SecurityConfig(IpValidationFilter ipValidationFilter, JwtFilter JwtFilter) {
        this.ipValidationFilter = ipValidationFilter;
        this.jwtFilter = JwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(ipValidationFilter, JwtFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();
    }




}
