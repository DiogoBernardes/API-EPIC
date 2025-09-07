package com.epic.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidade que representa as transações feitas pelo utilizador.
 * <p>
 * Mapeada para a tabela <code>transactions</code> na base de dados.
 * </p>
 *
 * <ul>
 *     <li>Armazena o tipo de transação(Débito ou Crédito) e a conta onde foi realizada.</li>
 *     <li>Armazena o valor, a data e uma descrição da transação, .</li>
 *     <li>Permite ao utilizador marcar uma transação como recorrente.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
@Where(clause = "removed_at IS NULL")
public class Transaction {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @ColumnDefault("false")
    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne(mappedBy = "transaction")
    private RecurringSetting recurringSetting;

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