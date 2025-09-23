package com.epic.finance.dto.category;

import com.epic.shared.enums.CategoryType;
import lombok.Data;

/**
 * DTO para atualização parcial de uma categoria.
 * <p>
 * Apenas os campos preenchidos serão atualizados. Campos nulos serão ignorados,
 * mantendo os valores existentes na entidade.
 * Este objeto é enviado pelo cliente para o endpoint <code>/finance/category/update/{categoryId}</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class UpdateCategoryDto {
    private String name;
    private CategoryType type;
}
