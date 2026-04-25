package com.SplitSewa.controller;

import com.SplitSewa.dto.expense.AddExpenseRequest;
import com.SplitSewa.dto.expense.BalanceResponse;
import com.SplitSewa.dto.expense.ExpenseResponse;
import com.SplitSewa.dto.expense.SettlementRequest;
import com.SplitSewa.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/expense")
    public ResponseEntity<String>addExpenses(@RequestBody @Valid AddExpenseRequest expenseRequest, Authentication auth){
        return ResponseEntity.ok(expenseService.addExpense(expenseRequest,auth.getName()));
    }

    @GetMapping("/groups/{id}/expenses")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(@PathVariable @Valid Long id, Authentication auth){
        return ResponseEntity.ok(expenseService.seeTotalExpenses(id,auth.getName()));
    }

    @GetMapping("/groups/{id}/balances")
    public ResponseEntity<List<BalanceResponse>> getBalances(@PathVariable @Valid Long id, Authentication auth){
        return ResponseEntity.ok(expenseService.seeExpenses(id,auth.getName()));
    }

    @PostMapping("/settlements/pay")
    public ResponseEntity<String> settleExpense(@RequestBody @Valid SettlementRequest req, Authentication auth) {
        return ResponseEntity.ok(expenseService.settleExpense(req, auth.getName()));
    }


}
