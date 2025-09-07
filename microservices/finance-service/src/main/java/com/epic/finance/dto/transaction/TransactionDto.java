package com.epic.finance.dto.transaction;

import com.epic.finance.dto.account.AccountDto;
import com.epic.finance.dto.category.CategoryDto;
import com.epic.shared.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO que representa as informações relativas a uma determinada transação do utilizador.
 * Este objeto é pedido pelo cliente pelo o endpoint
 * <code>/finance/transaction/{transactionId}</code> ou <code>/finance/transaction</code> .
 *
 * <br>
 * {@code @Diogo Bernardes}
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private UUID id;
    private UserInfoDto user;
    private AccountDto account;
    private CategoryDto category;
    private String description;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private Boolean isRecurring;
}
