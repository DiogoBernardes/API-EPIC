package com.epic.finance.controller;

import com.epic.finance.dto.PageResponse;
import com.epic.finance.dto.transaction.CreateTransactionDto;
import com.epic.finance.dto.transaction.TransactionDto;
import com.epic.finance.dto.transaction.UpdateTransactionDto;
import com.epic.finance.service.TransactionService;
import com.epic.shared.security.JwtHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Controller REST responsável pela gestão das transações do utilizador.
 * <p>
 * Todos os endpoints requerem autenticação via JWT.
 * O ID do utilizador é extraído automaticamente do token JWT enviado
 * no cabeçalho Authorization.
 * </p>
 *
 * Funcionalidades principais:
 * <ul>
 *     <li>Obter todas as transações do utilizador, com possibilidade de filtragem por conta, categoria e intervalo de datas.</li>
 *     <li>Obter uma transação específica pelo seu ID.</li>
 *     <li>Criar uma nova transação.</li>
 *     <li>Atualizar uma transação existente.</li>
 *     <li>Eliminar uma transação.</li>
 * </ul>
 *
 * A listagem de transações suporta paginação e ordenação através do retorno de {@link PageResponse}.
 * {@code @Diogo Bernardes}
 */
@RestController
@RequestMapping("/finance/transaction")
@Tag(name = "Transactions", description = "Gestão das transações do Utilizador")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtHelper jwtHelper;

    /**
     * Obter todas as transações do utilizador, podendo filtrar por conta, categoria ou intervalo de datas.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @param accountId (Opcional) ID da conta para filtrar transações.
     * @param categoryId (Opcional) ID da categoria para filtrar transações.
     * @param startDate (Opcional) Data de início do filtro.
     * @param endDate (Opcional) Data de fim do filtro.
     * @return {@link PageResponse} com a lista de {@link TransactionDto}, suportando paginação.
     */
    @GetMapping
    public ResponseEntity<PageResponse<TransactionDto>> getTransactions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "accountId", required = false) UUID accountId,
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {

        UUID userId = jwtHelper.extractUserId(authHeader);

        Page<TransactionDto> page = transactionService.getTransactions(userId, accountId, categoryId, startDate, endDate);

        PageResponse<TransactionDto> response = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Obter uma transação específica pelo seu ID.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @param transactionId ID da transação a ser obtida.
     * @return {@link TransactionDto} com os detalhes da transação.
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(@RequestHeader("Authorization") String authHeader,
                                                             @PathVariable("transactionId") UUID transactionId) {

        UUID userId = jwtHelper.extractUserId(authHeader);
        return ResponseEntity.ok(transactionService.getTransactionById(userId, transactionId));
    }

    /**
     * Criar uma nova transação para o utilizador logado e para uma determinada conta.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @param dto Dados da nova transação encapsulados em {@link CreateTransactionDto}.
     * @return {@link TransactionDto} com a transação criada.
     */
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestHeader("Authorization") String authHeader,
                                                            @RequestBody CreateTransactionDto dto) {

        UUID userId = jwtHelper.extractUserId(authHeader);
        return ResponseEntity.ok(transactionService.createTransaction(userId, dto));
    }

    /**
     * Atualiza uma transação existente.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @param transactionId ID da transação a ser atualizada.
     * @param dto Dados atualizados da transação encapsulados em {@link UpdateTransactionDto}.
     * @return {@link TransactionDto} com os dados atualizados da transação.
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(@RequestHeader("Authorization") String authHeader,
                                                            @PathVariable("transactionId") UUID transactionId,
                                                            @RequestBody UpdateTransactionDto dto) {

        UUID userId = jwtHelper.extractUserId(authHeader);
        return ResponseEntity.ok(transactionService.updateTransaction(userId, transactionId, dto));
    }
    
    /**
     * Elimina uma transação existente.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @param transactionId ID da transação a ser eliminada.
     * @return ResponseEntity sem conteúdo (204) em caso de sucesso.
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@RequestHeader("Authorization") String authHeader,
                                                  @PathVariable("transactionId") UUID transactionId) {

        UUID userId = jwtHelper.extractUserId(authHeader);
        transactionService.deleteTransaction(transactionId, userId);
        return ResponseEntity.noContent().build();
    }
}
