package com.epic.finance.dto.transaction;

import com.epic.shared.enums.CommonFlag;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO utilizado para receber os dados necessários
 * para o registo de uma nova transação.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/finance/transaction/create</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Data
public class CreateTransactionDto {
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private CommonFlag isRecurring;
}
