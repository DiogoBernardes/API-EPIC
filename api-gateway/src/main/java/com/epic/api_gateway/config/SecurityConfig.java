package com.epic.api_gateway.config;

import com.epic.api_gateway.filter.JwtAuthenticationFilter;
import com.epic.shared.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe de configuração de segurança do Spring Security para a API Gateway.
 * <p>
 * Esta configuração define como as requisições HTTP serão tratadas no que diz respeito
 * à autenticação e autorização, utilizando autenticação via JWT (JSON Web Token).
 * </p>
 *
 * <ul>
 *     <li>Desativa a proteção CSRF (não necessária para APIs REST stateless).</li>
 *     <li>Permite acesso público a endpoints sob o path <code>/auth/**</code>.</li>
 *     <li>Exige autenticação para qualquer outro endpoint.</li>
 *     <li>Adiciona um filtro de autenticação JWT antes do filtro padrão de autenticação
 *         do Spring Security ({@link UsernamePasswordAuthenticationFilter}).</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Configura a cadeia de filtros de segurança do Spring Security.
     * <p>
     * Nesta configuração:
     * <ul>
     *     <li>CSRF é desativado para permitir funcionamento de APIs REST.</li>
     *     <li>O endpoint <code>/auth/**</code> é liberado sem autenticação.</li>
     *     <li>Todos os outros endpoints requerem autenticação JWT válida.</li>
     *     <li>O filtro {@link JwtAuthenticationFilter} é adicionado antes do filtro padrão
     *         {@link UsernamePasswordAuthenticationFilter} para processar o token JWT
     *         presente no cabeçalho da requisição.</li>
     * </ul>
     * </p>
     *
     * @param http objeto {@link HttpSecurity} usado para configurar regras de segurança.
     * @return instância de {@link SecurityFilterChain} com as regras de segurança definidas.
     * @throws Exception se ocorrer algum erro na configuração da segurança.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(  "/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Expõe o {@link AuthenticationManager} como um bean do Spring para ser injetado
     * em serviços e componentes que precisem autenticar utilizadores manualmente.
     * <p>
     * O {@link AuthenticationManager} é obtido a partir de {@link AuthenticationConfiguration},
     * que contém toda a configuração de autenticação da aplicação.
     * </p>
     *
     * <p><b>Uso comum:</b> Utilizado em serviços de autenticação para processar credenciais
     * de login e retornar um objeto {@link org.springframework.security.core.Authentication}.</p>
     *
     * @param config configuração de autenticação atual da aplicação.
     * @return instância configurada de {@link AuthenticationManager}.
     * @throws Exception se ocorrer erro ao criar o {@link AuthenticationManager}.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
