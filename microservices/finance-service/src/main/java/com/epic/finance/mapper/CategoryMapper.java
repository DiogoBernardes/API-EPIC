package com.epic.finance.mapper;

import com.epic.finance.dto.category.CategoryDto;
import com.epic.finance.entity.Category;
import com.epic.shared.dto.UserInfoDto;
import org.springframework.stereotype.Component;


/**
 * Mapper responsável por converter entidades {@link Category} em {@link CategoryDto}.
 * <p>
 * Centraliza a lógica de conversão para manter o {@link com.epic.finance.service.TransactionService}
 * e outros serviços limpos, evitando repetição de código.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Converte uma {@link Category} em {@link CategoryDto}, incluindo informações do utilizador.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Component
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category, UserInfoDto userInfo) {
        return CategoryDto.builder()
                .id(category.getId())
                .user(userInfo)
                .name(category.getName())
                .type(category.getType())
                .build();
    }
}
