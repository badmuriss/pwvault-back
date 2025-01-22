package com.outis.pwvault.config;

import com.outis.pwvault.filter.IpValidationFilter;
import com.outis.pwvault.filter.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final IpValidationFilter ipValidationFilter;
    private final TokenFilter tokenFilter;

    public SecurityConfig(IpValidationFilter ipValidationFilter, TokenFilter tokenFilter) {
        this.ipValidationFilter = ipValidationFilter;
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(ipValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tokenFilter, IpValidationFilter.class);

        return http.build();
    }
}
