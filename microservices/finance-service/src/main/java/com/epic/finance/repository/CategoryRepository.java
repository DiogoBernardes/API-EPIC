package com.epic.finance.repository;

import com.epic.finance.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
    Optional<Category> findByNameAndUserId(String name, UUID userId);
}
