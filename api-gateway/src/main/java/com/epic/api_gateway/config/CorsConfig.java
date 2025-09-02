package com.epic.api_gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Configuração global de CORS para o API Gateway.
 * <p>
 * Permite que requisições de diferentes origens acedam à API,
 * definindo métodos HTTP, cabeçalhos e credenciais permitidos.
 * </p>
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Permite todas as origens.</li>
 *     <li>Habilita métodos GET, POST, PUT, DELETE e OPTIONS.</li>
 *     <li>Permite todos os cabeçalhos e envio de credenciais.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Configuration
public class CorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") //Em Produção não esquecer de alterar para os dominios que vão consumir a API
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
