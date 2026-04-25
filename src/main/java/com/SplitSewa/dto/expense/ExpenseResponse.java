package com.SplitSewa.dto.expense;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal amount;  // you need to fix Long → BigDecimal
    private String paidBy;
    private Long groupId;
    private LocalDateTime createdAt;
}