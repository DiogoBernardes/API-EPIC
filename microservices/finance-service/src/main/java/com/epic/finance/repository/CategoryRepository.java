package com.epic.finance.repository;

import com.epic.finance.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de persistência da entidade {@link Category}.
 * <p>
 * Este repositório estende {@link JpaRepository}, fornecendo operações CRUD padrão
 * e inclui métodos customizados para procurar categorias por utilizador, nome e estado,
 * respeitando o soft delete.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Obter todas as categorias de um utilizador específico (ignora categorias deletadas).</li>
 *     <li>Obter uma categoria pelo seu ID associado a um utilizador.</li>
 *     <li>Obter uma categoria pelo nome associado a um utilizador específico.</li>
 *     <li>Aplicar soft delete numa categoria via query customizada.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByUserIdAndRemovedAtIsNull(UUID userId);
    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
    Optional<Category> findByNameAndUserIdAndRemovedAtIsNull(String name, UUID userId);
    @Modifying
    @Query("UPDATE Category c SET c.removedAt = CURRENT_TIMESTAMP WHERE c.id = :id AND c.userId = :userId")
    void softDeleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
