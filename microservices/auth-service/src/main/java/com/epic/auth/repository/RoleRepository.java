package com.epic.auth.repository;

import com.epic.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório JPA para a entidade {@link Role}.
 * <p>
 * Fornece operações de acesso a dados para a tabela <code>roles</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findRoleById(UUID id);
}

