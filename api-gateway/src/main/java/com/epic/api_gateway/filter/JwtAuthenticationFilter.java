package com.epic.api_gateway.filter;

import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.UUID;

/**
 * Filtro global de autenticação JWT para o API Gateway.
 * <p>
 * Esta classe intercepta todas as requisições que passam pelo Gateway e valida
 * o token JWT presente no cabeçalho Authorization. Caso o token seja válido,
 * insere o utilizador autenticado no contexto de segurança reativo do Spring Security.
 * </p>
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Ignora endpoints públicos, como login, registo e a documentação do Swagger.</li>
 *     <li>Valida tokens JWT utilizando {@link com.epic.shared.security.JwtUtil}.</li>
 *     <li>Extrai informações do utilizador (ID, email, nome, role) e popula o contexto de segurança.</li>
 *     <li>Bloqueia requisições sem token ou com token inválido, retornando 401 Unauthorized.</li>
 * </ul>
 * </p>
 * <p>
 * Aplicação:
 * <ul>
 *     <li>Todos os microserviços protegidos pelo API Gateway que requerem autenticação JWT.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Intercepta cada requisição que passa pelo Gateway.
     * <p>
     * Funcionalidades:
     * <ul>
     *     <li>Ignora endpoints públicos como login, registo e Swagger.</li>
     *     <li>Valida o token JWT presente no cabeçalho Authorization.</li>
     *     <li>Popula o {@link SecurityContextImpl} com informações do utilizador autenticado.</li>
     *     <li>Retorna 401 Unauthorized para requisições sem token ou com token inválido.</li>
     * </ul>
     * </p>
     *
     * @param exchange contexto da requisição HTTP.
     * @param chain   cadeia de filtros a ser continuada caso a autenticação seja válida.
     * @return {@link Mono<Void>} indicando a conclusão do processamento da requisição.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Ignores Login/Register and Swagger paths
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register") || path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") || path.equals("/swagger-ui.html") ||
                path.startsWith("/webjars") || path.equals("/v3/api-docs/aggregate")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange);
        }

        Claims claims = jwtUtil.extractClaims(token);

        UserInfoDto userInfo = new UserInfoDto();

        String roleId = claims.get("role_id", String.class);
        String roleName = claims.get("role", String.class);

        userInfo.setId(UUID.fromString(claims.get("id", String.class)));
        userInfo.setRoleId(UUID.fromString(claims.get("role_id", String.class)));
        userInfo.setEmail(claims.get("email", String.class));
        userInfo.setFirstName(claims.get("firstname", String.class));
        userInfo.setLastName(claims.get("lastname", String.class));
        userInfo.setRoleName(roleName);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userInfo, token, Collections.singletonList(new SimpleGrantedAuthority(roleId)));

        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }


    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * Retorna uma resposta HTTP 401 Unauthorized para requisições não autenticadas.
     *
     * @param exchange contexto da requisição HTTP.
     * @return {@link Mono<Void>} indicando a conclusão da resposta.
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
