package com.epic.shared.enums;

/**
 * Enumeração que representa valores booleanos de forma semântica.
 * <p>
 * Valores possíveis:
 * <ul>
 *     <li>{@code TRUE} – representa valor verdadeiro.</li>
 *     <li>{@code FALSE} – representa valor falso.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
public enum CommonFlag {
    TRUE,
    FALSE;

    @Override
    public String toString() {
        return name();
    }

    public static CommonFlag fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean toBoolean() {
        return this == TRUE;
    }
}
