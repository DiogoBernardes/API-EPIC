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
 * Entidade que representa roles de utilizadores,
 * como Admin, User ou Premium.
 * <p>
 * Mapeada para a tabela <code>roles</code> na base de dados.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(generator = "UUID")
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

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    @OneToMany(mappedBy = "role")
    private Set<User> users = new LinkedHashSet<>();

}