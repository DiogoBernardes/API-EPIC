package com.epic.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuração de segurança do API Gateway através do Spring WebFlux Security.
 * <p>
 * Esta classe define as regras de autenticação e autorização para os endpoints
 * do gateway, permitindo acesso público aos endpoints de login, registo e
 * documentação Swagger, enquanto protege os demais endpoints.
 * </p>
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Desabilita CSRF, HTTP Basic e Form Login.</li>
 *     <li>Permite acesso público a endpoints de autenticação e Swagger.</li>
 *     <li>Exige autenticação para todos os outros endpoints.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/auth/**",
                                "/finance/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs/swagger-config",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }
}
