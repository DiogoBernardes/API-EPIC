package com.epic.auth.repository;

import com.epic.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;


/**
 * Repositório para operações de acesso e manipulação da entidade {@link User}.
 * <p>
 * Fornece métodos para realizar consultas e persistência de dados
 * na tabela <code>users</code>, utilizando o Spring Data JPA.
 * </p>
 *
 * <ul>
 *     <li>Extende {@link JpaRepository} para operações CRUD padrão.</li>
 *     <li>Inclui método customizado para busca de utilizador por e-mail.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
