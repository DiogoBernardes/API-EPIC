package com.epic.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Classe de configuração de segurança do módulo de autenticação.
 * <p>
 * Define e expõe o {@link PasswordEncoder} como um bean do Spring,
 * utilizando a implementação {@link BCryptPasswordEncoder} para
 * encriptar a password de forma segura.
 * </p>
 *
 * <ul>
 *     <li>O BCrypt aplica um algoritmo de hash adaptativo, tornando-o
 *         mais resistente a ataques de Brute Force.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
