package com.SplitSewa.dto.expense;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceResponse {
    private Long fromUserId;
    private String fromUsername;
    private Long toUserId;
    private String toUsername;
    private BigDecimal amount;
}