package com.SplitSewa.dto.expense;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpenseSplitResponse {
    private Long userId;
    private String username;
    private BigDecimal amountOwed;
        private boolean settled;


}
