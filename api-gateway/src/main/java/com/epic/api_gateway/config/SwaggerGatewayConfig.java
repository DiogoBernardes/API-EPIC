package com.epic.api_gateway.config;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;

/**
 * Configuração do Swagger/OpenAPI para o API Gateway.
 * <p>
 * Esta classe agrega a documentação dos microserviços conectados ao gateway,
 * configurando grupos de APIs, URLs dos servidores e autenticação via JWT.
 * </p>
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Define informações gerais da API, como título, descrição e versão.</li>
 *     <li>Configura segurança global via Bearer JWT.</li>
 *     <li>Agrupa endpoints de cada microserviço com suas URLs correspondentes.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Configuration
public class SwaggerGatewayConfig {

    /**
     * Configura as informações gerais do Swagger/OpenAPI, incluindo título,
     * descrição, versão e esquema de segurança JWT.
     *
     * @return um objeto {@link OpenAPI} com as informações e segurança configuradas
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway Swagger Aggregator")
                        .description("Aggregated documentation of microservices")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * Os restantes métodos configuram o grupo de APIs para os microserviços.
     *
     * @return um {@link GroupedOpenApi} configurado para "/microserviceName/**" no servidor localhost:808x
     */
    @Bean
    public GroupedOpenApi aiService() {
        return GroupedOpenApi.builder()
                .group("ai-service")
                .pathsToMatch("/ai/**")
                .addOpenApiCustomiser(openApi -> openApi.setServers(
                        List.of(new Server().url("http://localhost:8081"))
                ))
                .build();
    }

    @Bean
    public GroupedOpenApi authService() {
        return GroupedOpenApi.builder()
                .group("auth-service")
                .pathsToMatch("/auth/**")
                .addOpenApiCustomiser(openApi -> openApi.setServers(
                        List.of(new Server().url("http://localhost:8082"))
                ))
                .build();
    }

    @Bean
    public GroupedOpenApi financeService() {
        return GroupedOpenApi.builder()
                .group("finance-service")
                .pathsToMatch("/finance/**")
                .addOpenApiCustomiser(openApi -> openApi.setServers(
                        List.of(new Server().url("http://localhost:8083"))
                ))
                .build();
    }

    @Bean
    public GroupedOpenApi plannerService() {
        return GroupedOpenApi.builder()
                .group("planner-service")
                .pathsToMatch("/planner/**")
                .addOpenApiCustomiser(openApi -> openApi.setServers(
                        List.of(new Server().url("http://localhost:8084"))
                ))
                .build();
    }

    @Bean
    public GroupedOpenApi reportService() {
        return GroupedOpenApi.builder()
                .group("report-service")
                .pathsToMatch("/report/**")
                .addOpenApiCustomiser(openApi -> openApi.setServers(
                        List.of(new Server().url("http://localhost:8085"))
                ))
                .build();
    }
}
