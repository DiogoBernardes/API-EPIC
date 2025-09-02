package com.epic.finance.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade que representa as transações que são marcadas como recorrentes.
 * <p>
 * Mapeada para a tabela <code>recurring_settings</code> na base de dados.
 * </p>
 *
 * <ul>
 *     <li>Armazena qual a transação que é recorrente, a frequencia, dia e meses.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@Entity
@Table(name = "recurring_settings")
public class RecurringSetting {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @NotNull
    @Column(name = "frequency", nullable = false, length = 20)
    private String frequency;

    @Column(name = "day_of_month")
    private Integer dayOfMonth;

    @NotNull
    @Type(StringArrayType.class)
    @Column(name = "months", columnDefinition = "text[]")
    private String[] months;

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