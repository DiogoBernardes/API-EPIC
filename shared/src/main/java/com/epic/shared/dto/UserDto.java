package com.epic.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO que transporta informações de um utilizador.
 * <p>
 * Inclui dados básicos de perfil, credenciais e status da conta.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private UUID roleId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String password;
    private String country;
    private String status;
}

