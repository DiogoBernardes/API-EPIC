package com.epic.finance.dto;

import java.util.List;

/**
 * Representa uma resposta paginada para endpoints que retornam listas de dados.
 *
 * @param <T> Tipo dos elementos contidos na página.
 * {@code @Diogo Bernardes}
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last,
        boolean first
) {}
