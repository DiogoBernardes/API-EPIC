package com.epic.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidade que representa os diferentes tipos de utilizadores (Ex: Admin, User, Premium).
 * <p>
 * Mapeada para a tabela <code>roles</code> na base de dados.
 * </p>
 *
 * <ul>
 *     <li>Armazena o tipo de roles de utilizadores.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    @OneToMany(mappedBy = "role")
    private Set<User> users = new LinkedHashSet<>();

}