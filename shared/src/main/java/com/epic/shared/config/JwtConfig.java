package com.epic.shared.config;

import com.epic.shared.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do JWT para a aplicação.
 * <p>
 * Cria um bean {@link JwtUtil} configurado com a chave secreta e tempo de expiração
 * definidos no arquivo <code>application.yml</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Configuration
public class JwtConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    @Bean
    public JwtUtil jwtUtil(){
        return new JwtUtil(jwtSecret, jwtExpiration);
    }

}