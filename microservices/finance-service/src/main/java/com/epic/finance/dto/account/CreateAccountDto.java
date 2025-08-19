package com.epic.finance.dto.account;

import jakarta.validation.constraints.Pattern;
import java.util.UUID;

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
public class CreateAccountDto {
    private UUID userId;
    private String name;
    private Double balance = 0.0;
    @Pattern(regexp = "Active|Inactive", message = "Status must be Active or Inactive")
    private String status;
}
