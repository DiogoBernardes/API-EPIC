package com.epic.finance.entity;

import com.epic.shared.enums.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidade que representa as categorias de tipos de transações possiveis.
 * <p>
 * Mapeada para a tabela <code>categories</code> na base de dados.
 * </p>
 *
 * <ul>
 *     <li>Armazena o tipo de categoria associado à transação(Débito ou Crédito).</li>
 *     <li>Permite ao utilizador criar as suas próprias categorias.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
@Where(clause = "removed_at IS NULL")
public class Category {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private CategoryType type;

    @Column(name = "monthly_budget", precision = 15, scale = 2)
    private BigDecimal monthlyBudget;

    @OneToMany(mappedBy = "category")
    private Set<Transaction> transactions = new LinkedHashSet<>();

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