package com.epic.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidade que representa a conta associada a um utilizador. (Ex: Banco, Carteira, Paypal, revolut, etc..)
 * <p>
 * Mapeada para a tabela <code>accounts</code> na base de dados.
 * </p>
 *
 * <ul>
 *     <li>Armazena do tipo de conta associada ao utilizador.</li>
 *     <li>Armazena o saldo da conta.</li>
 *     <li>Inclui status para indicar se a conta está ativa ou inativa.</li>
 *     <li>Regista timestamps de criação, atualização e remoção (soft delete).</li>
 *     <li>Filtra pelas contas em que o removed_at é Nulo</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
@Where(clause = "removed_at IS NULL")
public class Account {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ColumnDefault("0.0")
    @Column(name = "balance", precision = 18, scale = 2)
    private BigDecimal balance;

    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "monthly_budget", precision = 15, scale = 2)
    private BigDecimal monthlyBudget;

    @NotNull
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