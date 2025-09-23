package com.epic.finance.controller;

import com.epic.finance.dto.category.CategoryDto;
import com.epic.finance.dto.category.CreateCategoryDto;
import com.epic.finance.dto.category.UpdateCategoryDto;
import com.epic.finance.service.CategoryService;
import com.epic.shared.security.JwtHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST responsável pela gestão das categorias de um utilizador.
 * <p>
 * Este controlador expõe endpoints para operações CRUD sobre {@link CategoryDto},
 * garantindo que todas as ações estão associadas a um utilizador autenticado
 * através do {@link JwtHelper}.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Listar todas as categorias do utilizador autenticado.</li>
 *     <li>Obter detalhes de uma categoria específica por ID.</li>
 *     <li>Criar uma nova categoria associada ao utilizador.</li>
 *     <li>Atualizar uma categoria existente.</li>
 *     <li>Remover (soft delete) uma categoria existente.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@RestController
@RequestMapping("/finance/category")
@Tag(name = "Categories", description = "Gestão das categorias do Utilizador")
@RequiredArgsConstructor
public class CategoryController {

    public final CategoryService categoryService;
    public final JwtHelper jwtHelper;

    /**
     * Obtém todas as categorias associadas ao utilizador autenticado.
     *
     * @param authHeader cabeçalho de autorização contendo o token JWT.
     * @return lista de categorias do utilizador.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAccounts(@RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        List<CategoryDto> categories = categoryService.getUserCategories(userId);
        return ResponseEntity.ok(categories);
    }

    /**
     * Obtém uma categoria específica pelo seu ID, associada ao utilizador autenticado.
     *
     * @param authHeader cabeçalho de autorização contendo o token JWT.
     * @param categoryId ID da categoria.
     * @return detalhes da categoria encontrada.
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@RequestHeader("Authorization") String authHeader,
                                                       @PathVariable("categoryId") UUID categoryId) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        CategoryDto category = categoryService.getUserCategoryById(userId, categoryId);
        return ResponseEntity.ok(category);
    }

    /**
     * Cria uma nova categoria associada ao utilizador autenticado.
     *
     * @param authHeader cabeçalho de autorização contendo o token JWT.
     * @param dto        dados necessários para criar a categoria.
     * @return categoria criada.
     */
    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(@RequestHeader("Authorization") String authHeader,
                                                      @RequestBody CreateCategoryDto dto) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        return ResponseEntity.ok(categoryService.createCategory(dto, userId));
    }

    /**
     * Atualiza uma categoria existente associada ao utilizador autenticado.
     *
     * @param authHeader cabeçalho de autorização contendo o token JWT.
     * @param categoryId ID da categoria a ser atualizada.
     * @param dto        dados para atualização da categoria.
     * @return categoria atualizada.
     */
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@RequestHeader("Authorization") String authHeader,
                                                      @PathVariable("categoryId") UUID categoryId,
                                                      @RequestBody UpdateCategoryDto dto) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        return ResponseEntity.ok(categoryService.updateCategory(dto, userId, categoryId));
    }

    /**
     * Remove (soft delete) uma categoria existente do utilizador autenticado.
     *
     * @param authHeader cabeçalho de autorização contendo o token JWT.
     * @param categoryId ID da categoria a ser removida.
     * @return resposta sem conteúdo em caso de sucesso.
     */
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable("categoryId") UUID categoryId) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
