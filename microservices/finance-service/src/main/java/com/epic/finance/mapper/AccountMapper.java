package com.epic.finance.mapper;

import com.epic.finance.dto.account.AccountDto;
import com.epic.finance.entity.Account;
import com.epic.shared.dto.UserInfoDto;
import org.springframework.stereotype.Component;

/**
 * Mapper responsável por converter entidades {@link Account} em {@link AccountDto}.
 * <p>
 * Centraliza a lógica de conversão para manter serviços como {@link com.epic.finance.service.TransactionService}
 * limpos e sem repetição de código.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Converte uma {@link Account} em {@link AccountDto}, incluindo informações do utilizador.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Component
public class AccountMapper {
    public AccountDto toAccountDto(Account account, UserInfoDto userInfo) {
        return AccountDto.builder()
                .id(account.getId())
                .user(userInfo)
                .name(account.getName())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }
}
