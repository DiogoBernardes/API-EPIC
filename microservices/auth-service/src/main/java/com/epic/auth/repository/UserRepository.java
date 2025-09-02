package com.epic.auth.repository;

import com.epic.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para a entidade {@link User}.
 * <p>
 * Inclui operação customizada para procurar o utilizador por e-mail.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
