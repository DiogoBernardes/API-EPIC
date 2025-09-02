package com.epic.finance.exception;

import com.epic.finance.exception.account.AccountNotFoundException;
import com.epic.finance.exception.account.ExistingAccountNameException;
import com.epic.shared.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Manipulador global de exceções do microserviço Finance.
 */
@ControllerAdvice(basePackages = "com.epic.finance")
public class FinanceExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ExistingAccountNameException.class)
    public ResponseEntity<Map<String, Object>> handleExistingAccountName(ExistingAccountNameException ex) {
        return buildResponse(HttpStatus.PRECONDITION_FAILED, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(LocalDateTime.now(), status.value(), message, null).toMap());
    }
}
