package com.epic.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing) para a aplicação.
 * <p>
 * Esta classe define quais origens, métodos HTTP e cabeçalhos são permitidos
 * nas requisições entre diferentes domínios. Também habilita o uso de credenciais.
 * </p>
 * <p>
 * A configuração aplicada permite:
 * <ul>
 *     <li>Origem: http://localhost:8080</li>
 *     <li>Métodos: GET, POST, PUT, DELETE, OPTIONS</li>
 *     <li>Todos os cabeçalhos</li>
 *     <li>Uso de credenciais (cookies, autorização, etc.)</li>
 * </ul>
 * </p>
 *
 *  {@code @Diogo Bernardes}
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
