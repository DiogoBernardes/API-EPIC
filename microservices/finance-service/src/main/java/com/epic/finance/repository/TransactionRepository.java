package com.epic.finance.repository;

import com.epic.finance.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repositório para operações de persistência da entidade {@link Transaction}.
 * <p>
 * Este repositório estende {@link JpaRepository} fornecendo operações CRUD padrão
 * e inclui métodos customizados para consultar transações com filtros complexos
 * e aplicar soft delete.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Obter transações de um utilizador com filtros opcionais por conta, categoria e intervalo de datas.</li>
 *     <li>Permitir paginação e ordenação através de {@link Pageable}.</li>
 *     <li>Aplicar soft delete em transações, definindo o campo {@code removedAt}.</li>
 * </ul>
 * {@code @Diogo Bernardes}
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.userId = :userId
          AND t.removedAt IS NULL
          AND (COALESCE(:accountId, t.account.id) = t.account.id)
          AND (COALESCE(:categoryId, t.category.id) = t.category.id)
          AND (COALESCE(:startDate, t.transactionDate) <= t.transactionDate)
          AND (COALESCE(:endDate, t.transactionDate) >= t.transactionDate)
       """)
    Page<Transaction> findTransactionsByFilters(@Param("userId") UUID userId, @Param("accountId") UUID accountId,
                                                @Param("categoryId") UUID categoryId, @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate, Pageable pageable);

    @Modifying
    @Query("UPDATE Transaction t SET t.removedAt = CURRENT_TIMESTAMP WHERE t.id = :id AND t.userId = :userId")
    void softDeleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

}
