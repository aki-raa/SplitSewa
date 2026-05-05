package com.SplitSewa.dto.expense;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class AddExpenseRequest {
    private Long groupId;
    private BigDecimal amount;
    private String description;
    private String category;  // FOOD, TRANSPORT, HOTEL, SHOPPING, OTHER
    private String splitType; // EQUAL or CUSTOM
    private Map<Long, BigDecimal> customSplits; // userId -> amount (only for CUSTOM)
}