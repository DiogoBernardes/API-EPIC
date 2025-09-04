package com.epic.finance.dto.account;

import com.epic.shared.enums.CommonStatus;
import lombok.Data;

/**
 * DTO utilizado para receber os dados necessários
 * para a atualização do estado de uma conta existente.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/finance/accounts/{id}/status</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class UpdateAccountStatusDto {
    private CommonStatus status;
}
