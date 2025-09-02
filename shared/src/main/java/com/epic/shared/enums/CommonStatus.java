package com.epic.shared.enums;

/**
 * Enumeração que representa o status comum das entidades.
 * <p>
 * Valores possíveis:
 * <ul>
 *     <li>{@code Active} – indica que a entidade está ativa.</li>
 *     <li>{@code Inactive} – indica que a entidade está inativa.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

public enum CommonStatus {
    Active,
    Inactive;

    @Override
    public String toString() {
        return name(); // Garante que o status é sempre “Active” ou “Inactive”
    }
}
