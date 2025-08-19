package com.epic.finance.entity;

import com.epic.auth.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //Desta forma damos possibilidade ao utilizador de criar as suas próprias categorias

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    @OneToMany(mappedBy = "category")
    private Set<Budget> budgets = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category")
    private Set<Transaction> transactions = new LinkedHashSet<>();

}