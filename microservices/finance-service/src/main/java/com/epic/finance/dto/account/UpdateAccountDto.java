package com.epic.finance.dto.account;

import jakarta.validation.constraints.Pattern;

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
public class UpdateAccountDto {
    private String name;
    private Double balance;
    @Pattern(regexp = "Active|Inactive", message = "Status must be Active or Inactive")
    private String status;
}
