package com.epic.finance.dto.account;

import lombok.Data;
import java.util.UUID;

/**
 * DTO que representa as informações relativas a uma determinada conta do utilizador.
 * Este objeto é pedido pelo cliente pelo o endpoint
 * <code>/finance/account/get/{id}</code>.
 *
 * <br>
 * {@code @Diogo Bernardes}
 */

@Data
public class AccountDto {
    private UUID id;
    private UUID userId;
    private String name;
    private Double balance;
    private String status;
}
