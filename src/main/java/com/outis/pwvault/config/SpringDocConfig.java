package com.outis.pwvault.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("PWVault API")
                        .description("API for Managing Azure KeyVault.")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes("bearerAuth", bearerTokenScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}