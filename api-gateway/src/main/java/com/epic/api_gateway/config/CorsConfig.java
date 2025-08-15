package com.epic.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing) para o API Gateway.
 * <p>
 * Esta classe define as origens, métodos HTTP e cabeçalhos permitidos nas requisições
 * entre diferentes domínios, além de habilitar o uso de credenciais.
 * </p>
 * <p>
 * Configuração aplicada:
 * <ul>
 *     <li>Origens permitidas: <a href="http://localhost:8080">API Gateway</a>,  <a href="http://localhost:8081">AI Microservice</a>,
 *      <a href="http://localhost:8082">Auth Microservice</a>,  <a href="http://localhost:8083">Finance Microservice</a>,
 *       <a href="http://localhost:8084">Planner Microservice</a> and  <a href="http://localhost:8085">Report Microservice</a></li>
 *     <li>Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS</li>
 *     <li>Todos os cabeçalhos são permitidos</li>
 *     <li>Uso de credenciais habilitado</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083",
                                "http://localhost:8084","http://localhost:8085")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
