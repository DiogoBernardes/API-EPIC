package com.epic.finance.dto.account;

import com.epic.shared.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO que representa as informações relativas a uma determinada conta do utilizador.
 * Este objeto é pedido pelo cliente pelo o endpoint
 * <code>/finance/account/get/{id}</code>.
 *
 * <br>
 * {@code @Diogo Bernardes}
 */


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private UserInfoDto user;
    private String name;
    private BigDecimal balance;
    private String status;
}
