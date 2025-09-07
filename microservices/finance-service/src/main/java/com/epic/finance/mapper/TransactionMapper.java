package com.epic.finance.mapper;

import com.epic.finance.dto.account.AccountDto;
import com.epic.finance.dto.category.CategoryDto;
import com.epic.finance.dto.transaction.TransactionDto;
import com.epic.finance.entity.Transaction;
import com.epic.shared.dto.UserInfoDto;
import org.springframework.stereotype.Component;

/**
 * Mapper responsável por converter entidades {@link Transaction} em {@link TransactionDto}.
 * <p>
 * Centraliza a lógica de conversão para manter o {@link com.epic.finance.service.TransactionService}
 * limpo e sem código repetitivo.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Converte uma {@link Transaction} para {@link TransactionDto} incluindo informações
 *     do utilizador, conta e categoria.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Component
public class TransactionMapper {
    public TransactionDto toTransactionDto(Transaction trans, UserInfoDto userInfo, AccountDto accountDto, CategoryDto categoryDto) {
        return TransactionDto.builder()
                .id(trans.getId())
                .user(userInfo)
                .account(accountDto)
                .category(categoryDto)
                .description(trans.getDescription())
                .amount(trans.getAmount())
                .transactionDate(trans.getTransactionDate())
                .isRecurring(trans.getIsRecurring())
                .build();
    }
}
