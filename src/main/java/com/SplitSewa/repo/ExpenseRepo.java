package com.SplitSewa.repo;

import com.SplitSewa.dto.expense.BalanceResponse;
import com.SplitSewa.model.Expense;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepo extends JpaRepository<Expense,Long> {
    List<BalanceResponse> findExpenseById(@Valid Long id);
    List<Expense> findByGroupId(Long groupId);

}
