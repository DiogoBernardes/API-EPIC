package com.epic.finance.repository;

import com.epic.finance.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de persistência da entidade {@link Account}.
 * <p>
 * Este repositório estende {@link JpaRepository} fornecendo operações CRUD padrão
 * e inclui métodos customizados para procurar contas por utilizador, nome e estado,
 * respeitando soft delete.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Obter todas as contas de um utilizador específico (ignora contas deletadas).</li>
 *     <li>Obter todas as contas com um determinado status.</li>
 *     <li>Obter uma conta pelo nome associada a um utilizador específico.</li>
 *     <li>Permitir desativar e aplicar soft delete em uma conta via query customizada.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByIdAndUserId(UUID accountId, UUID userId);

    Optional<Account> findByNameAndUserId(String name, UUID userId);

    List<Account> findByStatusAndUserId(String status, UUID userId);

    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.id = :accountId AND a.userId = :userId")
    void increaseBalance(@Param("accountId") UUID accountId, @Param("userId") UUID userId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance - :amount WHERE a.id = :accountId AND a.userId = :userId")
    void decreaseBalance(@Param("accountId") UUID accountId, @Param("userId") UUID userId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Account a SET a.status = :status, a.removedAt = CURRENT_TIMESTAMP " +
            "WHERE a.id = :id AND a.userId = :userId")
    void deactivateAndSoftDelete(UUID id, UUID userId, String status);
}

