package com.epic.shared.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtHelper {

    private final JwtUtil jwtUtil;

    /**
     * Extrai o ID do utilizador a partir do header Authorization.
     *
     * @param authorizationHeader header Authorization (Bearer token)
     * @return UUID do utilizador
     */
    public UUID extractUserId(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("User not authenticated!");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token invalid or expired");
        }

        Claims claims = jwtUtil.extractClaims(token);
        return UUID.fromString(claims.get("id", String.class));
    }
}
