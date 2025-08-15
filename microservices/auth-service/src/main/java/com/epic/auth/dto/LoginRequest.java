package com.epic.auth.dto;

import lombok.Data;

/**
 * DTO utilizado para receber os dados necessários
 * para o login do utilizador.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/auth/login</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}
