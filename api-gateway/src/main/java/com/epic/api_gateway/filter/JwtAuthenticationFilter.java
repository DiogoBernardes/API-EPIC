package com.epic.api_gateway.filter;

import com.epic.shared.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

/**
 * Filtro de autenticação JWT para o Spring Security.
 * <p>
 * Este filtro é executado uma única vez por requisição
 * ({@link OncePerRequestFilter}) e tem como objetivo:
 * </p>
 * <ul>
 *     <li>Ler o token JWT do cabeçalho <code>Authorization</code>.</li>
 *     <li>Validar o token utilizando {@link JwtUtil}.</li>
 *     <li>Extrair as claims (email, role) do token.</li>
 *     <li>Definir a autenticação do utilizador no contexto de segurança
 *         ({@link SecurityContextHolder}).</li>
 * </ul>
 *
 * <p>
 * Caso o token seja inválido, ausente ou mal formatado,
 * o filtro apenas prossegue a requisição sem definir autenticação.
 * </p>
 *
 * {@code @DiogoBernardes}
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Método chamado para processar cada requisição HTTP.
     * <p>
     * Passos executados:
     * </p>
     * <ol>
     *     <li>Obtém o cabeçalho <code>Authorization</code> da requisição.</li>
     *     <li>Verifica se começa com <code>Bearer </code> e extrai o token.</li>
     *     <li>Valida o token via {@link JwtUtil}.</li>
     *     <li>Se válido, extrai as claims e cria um objeto de autenticação
     *         ({@link UsernamePasswordAuthenticationToken}) com a role informada.</li>
     *     <li>Regista a autenticação no {@link SecurityContextHolder}.</li>
     *     <li>Encaminha a requisição para o próximo filtro na cadeia.</li>
     * </ol>
     *
     * @param request  objeto {@link HttpServletRequest} da requisição HTTP.
     * @param response objeto {@link HttpServletResponse} da resposta HTTP.
     * @param filterChain cadeia de filtros para continuar o processamento da requisição.
     * @throws ServletException se ocorrer um erro de servlet.
     * @throws IOException se ocorrer um erro de I/O.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.extractClaims(token);
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null,
                                Collections.singletonList(new SimpleGrantedAuthority(role)));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}