package com.epic.auth.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO utilizado para receber os dados necessários
 * para o registo de um novo utilizador.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/auth/register</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class RegisterRequestDto {
    private UUID roleId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String country;
}