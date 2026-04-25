package com.SplitSewa.repo;

import com.SplitSewa.dto.expense.BalanceResponse;
import com.SplitSewa.model.ExpenseSplit;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseSplitRepo extends JpaRepository<ExpenseSplit,Long> {
    List<BalanceResponse> findExpenseById(@Valid Long id);

    List<ExpenseSplit> findByUserIdAndExpense_GroupIdAndSettled(Long userId, Long groupId, boolean settled);

    List<ExpenseSplit> findByExpense_GroupIdAndSettled(Long groupId, boolean settled);
}
