package com.SplitSewa.dto.expense;

import lombok.Data;

import java.math.BigDecimal;

// SettlementRequest.java
@Data
public class SettlementRequest {
    private Long groupId;
    private Long toUserId;
    private BigDecimal amount;
}