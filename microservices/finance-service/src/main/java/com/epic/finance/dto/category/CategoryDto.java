package com.epic.finance.dto.category;

import com.epic.shared.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private UUID id;
    private UserInfoDto user;
    private String name;
    private String type;
    private BigDecimal monthlyBudget;
}
