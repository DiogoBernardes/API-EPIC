package com.epic.finance.dto.account;

import com.epic.shared.enums.CommonStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO utilizado para receber os dados necessários
 * para a atualização dos dados de uma conta existente.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/finance/account/update/{id}</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class UpdateAccountDto {
    private String name;
    private BigDecimal balance;
    private CommonStatus status;
}
