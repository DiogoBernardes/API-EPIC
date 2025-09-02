package com.epic.finance.dto.account;

import com.epic.shared.enums.CommonStatus;
import lombok.Data;

import java.math.BigDecimal;


/**
 * DTO utilizado para receber os dados necessários
 * para o registo de uma nova conta.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/finance/account/create</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class CreateAccountDto {
    private String name;
    private BigDecimal balance = BigDecimal.valueOf(0.0);
    private CommonStatus status;
}
