package com.epic.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;

/**
 * Configuração do Swagger/OpenAPI para o API Gateway.
 * <p>
 * Define informações gerais da API, versão e segurança via JWT para o Swagger UI.
 * </p>
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Configura título e versão da documentação.</li>
 *     <li>Adiciona segurança global via Bearer JWT.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Configuration
public class SwaggerGatewayConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info().title("API Gateway Swagger").version("1.0"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
