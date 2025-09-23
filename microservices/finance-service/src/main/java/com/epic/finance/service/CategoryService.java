package com.epic.finance.service;

import com.epic.finance.client.AuthClient;
import com.epic.finance.dto.category.CategoryDto;
import com.epic.finance.dto.category.CreateCategoryDto;
import com.epic.finance.dto.category.UpdateCategoryDto;
import com.epic.finance.entity.Category;
import com.epic.finance.exception.categories.ExistingCategoryException;
import com.epic.finance.exception.transaction.CategoryNotFoundException;
import com.epic.finance.mapper.CategoryMapper;
import com.epic.finance.repository.CategoryRepository;
import com.epic.shared.dto.UserInfoDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pela gestão das categorias.
 * <p>
 * Contém a lógica de negócio para criação, atualização, listagem e remoção
 * (soft delete) de categorias associadas a um utilizador.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Listar todas as categorias de um utilizador autenticado.</li>
 *     <li>Obter detalhes de uma categoria específica.</li>
 *     <li>Criar novas categorias, validando duplicados.</li>
 *     <li>Atualizar categorias existentes com validações de nome e tipo.</li>
 *     <li>Aplicar soft delete a categorias de um utilizador.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuthClient authClient;
    private final CategoryMapper categoryMapper;

    /**
     * Obtém todas as categorias ativas de um utilizador.
     *
     * @param userId ID do utilizador.
     * @return lista de categorias do utilizador.
     */
    public List<CategoryDto> getUserCategories(UUID userId) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        return categoryRepository.findAllByUserIdAndRemovedAtIsNull(userId)
                .stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .user(userInfo)
                        .name(category.getName())
                        .type(category.getType().toString())
                        .build())
                .toList();
    }

    /**
     * Obtém uma categoria específica pelo ID, pertencente a um utilizador.
     *
     * @param userId     ID do utilizador.
     * @param categoryId ID da categoria.
     * @return categoria encontrada.
     * @throws CategoryNotFoundException se a categoria não existir.
     */
    public CategoryDto getUserCategoryById(UUID userId, UUID categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found"));

        UserInfoDto userInfo = authClient.getUserById(userId);

        return CategoryDto.builder()
                .id(category.getId())
                .user(userInfo)
                .name(category.getName())
                .type(category.getType().toString())
                .build();
    }

    /**
     * Cria uma nova categoria para um utilizador, validando se já existe outra com o mesmo nome.
     *
     * @param dto    dados da nova categoria.
     * @param userId ID do utilizador.
     * @return categoria criada.
     * @throws ExistingCategoryException se já existir uma categoria com o mesmo nome.
     */
    public CategoryDto createCategory(CreateCategoryDto dto, UUID userId) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        categoryRepository.findByNameAndUserIdAndRemovedAtIsNull(dto.getName(), userId)
                .ifPresent(cat-> {
                    throw new ExistingCategoryException("There is already an category with that name.");
                });

        Category category = Category.builder()
                .userId(userId)
                .name(dto.getName())
                .type(dto.getType())
                .build();

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryDto(
                savedCategory,
                userInfo);
    }

    /**
     * Atualiza uma categoria existente, validando duplicados de nome e alterações de tipo.
     *
     * @param dto        dados de atualização.
     * @param userId     ID do utilizador.
     * @param categoryId ID da categoria a ser atualizada.
     * @return categoria atualizada.
     * @throws CategoryNotFoundException  se a categoria não existir.
     * @throws ExistingCategoryException  se já existir outra categoria com o mesmo nome.
     */
    public CategoryDto updateCategory(UpdateCategoryDto dto, UUID userId, UUID categoryId) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found"));

        if (dto.getName() != null) {
            categoryRepository.findByNameAndUserIdAndRemovedAtIsNull(dto.getName(), userId)
                    .ifPresent(cat -> {
                        if (!cat.getId().equals(categoryId)) {
                            throw new ExistingCategoryException("There is already a category with that name.");
                        }
                    });
            category.setName(dto.getName());
        }

        if (dto.getType() != null) {
            category.setType(dto.getType());
        }

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryDto(
                savedCategory,
                userInfo);
    }
    
    /**
     * Remove (soft delete) uma categoria de um utilizador.
     *
     * @param userId     ID do utilizador.
     * @param categoryId ID da categoria a ser removida.
     * @throws CategoryNotFoundException se a categoria não for encontrada ou não pertencer ao utilizador.
     */
    @Transactional
    public void deleteCategory(UUID userId, UUID categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found"));

        if(!category.getUserId().equals(userId)) {
            throw new CategoryNotFoundException("Category not found!");
        }

        categoryRepository.softDeleteByIdAndUserId(categoryId, userId);
    }
}
