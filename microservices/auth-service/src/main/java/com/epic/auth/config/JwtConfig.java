package com.epic.auth.config;

import com.epic.shared.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração responsável por instanciar e expor o {@link JwtUtil}
 * como um bean do Spring.
 * <p>
 * Os valores necessários para a configuração do JWT, como a chave secreta e
 * o tempo de expiração, são carregados a partir do arquivo de configuração
 * da aplicação <code>application.yml</code>)
 * utilizando a anotação {@link Value}.
 * </p>
 *
 * <ul>
 *     <li><b>security.jwt.secret</b>: chave secreta usada para assinar e validar tokens JWT.</li>
 *     <li><b>security.jwt.expiration</b>: tempo de expiração do token em milissegundos.</li>
 * </ul>
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