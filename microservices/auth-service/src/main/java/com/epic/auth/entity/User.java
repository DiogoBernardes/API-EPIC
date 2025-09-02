package com.epic.auth.entity;

import com.epic.shared.enums.CommonStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidade que representa um utilizador no sistema.
 * <p>
 * Mapeada para a tabela <code>users</code>.
 * </p>
 *
 * <p>Principais responsabilidades:</p>
 * <ul>
 *   <li>Armazena credenciais e dados de perfil.</li>
 *   <li>Regista metadados de criação, atualização e remoção lógica.</li>
 *   <li>Controla o estado da conta (ativa/inativa).</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 100)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Size(max = 100)
    @NotNull
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CommonStatus status;

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

}