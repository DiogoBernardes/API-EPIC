package com.epic.shared.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa a estrutura de resposta de erro da API.
 * <p>
 * Contém informações sobre o status HTTP, mensagem, timestamp e detalhes opcionais.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        Object details
) {
    public Map<String, Object> toMap() {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", timestamp);
        body.put("status", status);
        body.put("message", message);
        if (details != null) {
            body.put("details", details);
        }
        return body;
    }
}
