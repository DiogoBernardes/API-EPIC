package com.epic.finance.dto.category;

import com.epic.shared.enums.CategoryType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO utilizado para receber os dados necessários
 * para o registo de uma nova categoria.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/finance/category/create</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class CreateCategoryDto {
    private String name;
    private CategoryType type;
    private BigDecimal monthlyBudget;
}
