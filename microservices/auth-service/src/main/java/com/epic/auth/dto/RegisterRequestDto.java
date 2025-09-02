package com.epic.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para registo de novo utilizador.
 * <p>
 * Contém os dados enviados pelo cliente para o endpoint
 * <code>/auth/register</code>, incluindo credenciais e informações pessoais.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class RegisterRequestDto {
    private UUID roleId;
    private String email;
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters long, contain one uppercase letter, one number and one special character"
    )
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String country;
}