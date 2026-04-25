package com.SplitSewa.dto.expense;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddExpenseRequest {
    private Long groupId;
    private BigDecimal amount;
    private String description;


}
