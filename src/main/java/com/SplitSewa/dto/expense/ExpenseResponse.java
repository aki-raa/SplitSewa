package com.SplitSewa.dto.expense;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal amount;
    private String paidBy;
    private Long groupId;
    private String category;  // add this
    private String splitType; // add this
    private LocalDateTime createdAt;
}