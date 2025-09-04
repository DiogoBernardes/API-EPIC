package com.epic.finance.dto.account;

import lombok.Data;

/**
 * DTO utilizado para receber os dados necessários
 * para a atualização do nome de uma conta existente.
 * <p>
 * Este objeto é enviado pelo cliente para o endpoint
 * <code>/finance/accounts/{id}/name</code>.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@Data
public class UpdateAccountNameDto {
    private String name;
}

