package com.epic.finance.entity;

import com.epic.auth.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidade que representa os budgets definidos pelo utilizador.
 * <p>
 * Mapeada para a tabela <code>budget</code> na base de dados.
 * </p>
 *
 * <ul>
 *     <li>Armazena o utilizador e a conta a que o budget está associado.</li>
 *     <li>Armazena o valor do budget.</li>
 *     <li>Permite escolher o periodo em que o budget deve estar ativo.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

//Necessário alterar esta tabela!
@Getter
@Setter
@Entity
@Table(name = "budget")
public class Budget {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    @Column(name = "amount_limit", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountLimit;

    @Size(max = 20)
    @Column(name = "period", length = 20)
    private String period;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "removed_at")
    private Instant removedAt;



}