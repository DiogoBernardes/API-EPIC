package com.epic.shared.security;

import com.epic.shared.dto.UserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilitário para geração, validação e extração de tokens JWT.
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Gera tokens JWT a partir de informações de {@link UserInfoDto}.</li>
 *     <li>Extrai claims de um token JWT.</li>
 *     <li>Valida se um token é válido e não expirou.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Component
public class JwtUtil {

    private final String jwtSecret;
    private final long expirationTime;
    /** Chave criptográfica derivada da chave secreta para assinatura do token. */
    private final Key signingKey;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret,
                   @Value("${jwt.expiration}") long expirationTime) {
        this.jwtSecret = jwtSecret;
        this.expirationTime = expirationTime;
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Gera um token JWT a partir das informações de um utilizador.
     *
     * @param user objeto {@link UserInfoDto} contem os dados do utilizador.
     * @return token JWT gerado.
     */
    public String generateToken(UserInfoDto user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("id", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("firstname", user.getFirstName())
                .claim("lastname", user.getLastName())
                .claim("role_id", user.getRoleId().toString())
                .claim("role", user.getRoleName())
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