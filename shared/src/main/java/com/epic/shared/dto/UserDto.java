package com.epic.shared.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) que representa as informações de um utilizador
 * utilizadas para transferência de dados entre serviços e camadas da aplicação.
 * <p>
 * É usado para abstrair a entidade {@link com.epic.auth.entity.User} quando não
 * é necessário expor todos os detalhes internos, como a password ou dados sensíveis.
 * </p>
 *
 * <ul>
 *     <li>Transporta informações básicas de identificação e perfil do utilizador.</li>
 *     <li>Inclui status para indicar se a conta está ativa ou inativa.</li>
 *     <li>Pode ser utilizado tanto para autenticação quanto para comunicação entre microserviços.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private UUID id;
    private UUID roleId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String country;
    private String status;

}
