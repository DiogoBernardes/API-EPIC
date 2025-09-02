package com.epic.finance.repository.account;

import com.epic.finance.entity.Account;
import com.epic.shared.enums.CommonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de persistência da entidade {@link Account}.
 * <p>
 * Este repositório estende {@link JpaRepository} fornecendo operações CRUD padrão
 * e inclui métodos customizados para buscar contas por usuário, nome e status.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Obter todas as contas de um utilizador específico.</li>
 *     <li>Obter todas as contas com um determinado status.</li>
 *     <li>Obter uma conta pelo nome associada a um utilizador específico.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUserId(UUID userId);
    List<Account> findByStatus(CommonStatus status);
    Optional<Account> findByNameAndUserId(String name, UUID userId);
}
