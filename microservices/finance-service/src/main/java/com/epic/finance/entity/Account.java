package com.epic.finance.entity;

import com.epic.auth.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ColumnDefault("0.0")
    @Column(name = "balance", precision = 18, scale = 2)
    private BigDecimal balance;

    @Size(max = 50)
    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

}