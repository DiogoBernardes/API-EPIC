package com.epic.shared.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para transferência de informações básicas do utilizador.
 * <p>
 * Inclui dados de identificação, perfil e status da conta,
 * sem expor informações sensíveis como a password.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private UUID id;
    private UUID roleId;
    private String roleName;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String country;
    private String status;

}
