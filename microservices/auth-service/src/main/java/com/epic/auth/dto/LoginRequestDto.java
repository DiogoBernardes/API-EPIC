package com.epic.auth.dto;

import lombok.Data;

/**
 * DTO para requisições de login.
 * <p>
 * Contém as credenciais (email e password) enviadas pelo cliente
 * para o endpoint <code>/auth/login</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
