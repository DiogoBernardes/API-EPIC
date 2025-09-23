package com.epic.finance.dto.transaction;

import com.epic.shared.enums.CommonFlag;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para atualização parcial de uma transação.
 * <p>
 * Apenas os campos preenchidos serão atualizados. Campos nulos serão ignorados,
 * mantendo os valores existentes na entidade.
 * Este objeto é enviado pelo cliente para o endpoint <code>/finance/transaction/{transactionId}</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class UpdateTransactionDto {
    private UUID accountId;
    private UUID categoryId;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private CommonFlag isRecurring;
}
