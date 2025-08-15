package com.epic.shared.security;

import com.epic.shared.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 * Classe utilitária para criação, extração e validação de tokens JWT (JSON Web Token).
 * <p>
 * É responsável por:
 * <ul>
 *     <li>Gerar tokens JWT a partir de informações de um utilizador ({@link UserDto}).</li>
 *     <li>Extrair claims (informações) contidas em um token JWT.</li>
 *     <li>Validar se um token é assinado corretamente e ainda está no prazo de validade.</li>
 * </ul>
 * </p>
 *
 * <p>Os tokens são assinados utilizando algoritmo HS256 e uma chave secreta definida
 * nas configurações da aplicação.</p>
 *
 * {@code @Diogo Bernardes}
 */

public class JwtUtil {

    private final String jwtSecret;
    private final long expirationTime;
    /** Chave criptográfica derivada da chave secreta para assinatura do token. */
    private final Key signingKey;

    public JwtUtil(String jwtSecret, long expirationTime) {
        this.jwtSecret = jwtSecret;
        this.expirationTime = expirationTime;
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Gera um token JWT a partir das informações de um utilizador.
     *
     * @param user objeto {@link UserDto} contem os dados do utilizador.
     * @return token JWT gerado.
     */
    public String generateToken(UserDto user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRoleId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai as claims (informações) de um token JWT.
     *
     * @param token token JWT.
     * @return objeto {@link Claims} contem os dados extraídos do token.
     * @throws JwtException se o token for inválido ou estiver corrompido.
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida se um token JWT é assinado corretamente e não expirou.
     *
     * @param token token JWT.
     * @return {@code true} se o token for válido; {@code false} caso contrário.
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}