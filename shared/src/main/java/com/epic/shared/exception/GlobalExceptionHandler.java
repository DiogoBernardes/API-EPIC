package com.epic.shared.exception;

import com.epic.shared.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções da aplicação.
 * <p>
 * Centraliza o tratamento de erros, garantindo respostas consistentes para o cliente.
 * </p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Trata exceções de utilizadores inativos ({@link UserInactiveException}) com HTTP 403.</li>
 *     <li>Trata exceções de utilizador não encontrado ({@link UserNotFoundException}) com HTTP 404.</li>
 *     <li>Trata exceções gerais ({@link RuntimeException}) com HTTP 400.</li>
 *     <li>Trata erros de validação de campos ({@link MethodArgumentNotValidException}) e retorna detalhes por campo.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    // User Exceptions
    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleUserInactive(UserInactiveException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(LocalDateTime.now(), status.value(), message, null).toMap());
    }
}