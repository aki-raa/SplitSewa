package com.SplitSewa.service;

import com.SplitSewa.dto.expense.*;
import com.SplitSewa.model.*;
import com.SplitSewa.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseSplitRepo expenseSplitRepo;
    @Autowired
    private ExpenseRepo expenseRepo;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GroupMemberRepository groupMemberRepository;


    public String addExpense(AddExpenseRequest req, String email) {
        UserEntity user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new RuntimeException("You are not a member of this group");
        }

        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(user);
        expense.setAmount(req.getAmount());
        expense.setDescription(req.getDescription());
        expense.setCategory(req.getCategory() != null ? req.getCategory() : "OTHER");
        expenseRepo.save(expense);

        List<GroupMember> members = groupMemberRepository.findByGroupId(group.getId());

        if ("CUSTOM".equalsIgnoreCase(req.getSplitType()) && req.getCustomSplits() != null) {
            // validate total matches amount
            BigDecimal total = req.getCustomSplits().values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (total.compareTo(req.getAmount()) != 0) {
                throw new RuntimeException("Custom splits must add up to total amount. Got: " + total + ", Expected: " + req.getAmount());
            }
            // create splits per custom amount
            for (Map.Entry<Long, BigDecimal> entry : req.getCustomSplits().entrySet()) {
                UserEntity member = userRepo.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("User not found: " + entry.getKey()));
                ExpenseSplit split = new ExpenseSplit();
                split.setExpense(expense);
                split.setUser(member);
                split.setAmountOwed(entry.getValue());
                split.setSettled(false);
                expenseSplitRepo.save(split);
            }
            return "Expense added with custom split among " + req.getCustomSplits().size() + " members";
        } else {
            // EQUAL split
            BigDecimal splitAmount = req.getAmount()
                    .divide(BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);
            for (GroupMember member : members) {
                ExpenseSplit split = new ExpenseSplit();
                split.setExpense(expense);
                split.setUser(member.getUser());
                split.setAmountOwed(splitAmount);
                split.setSettled(false);
                expenseSplitRepo.save(split);
            }
            return "Expense added and split equally among " + members.size() + " members";
        }
    }
    public List<ExpenseResponse> seeTotalExpenses(Long id, String email) {
        UserEntity user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!groupMemberRepository.existsByGroupIdAndUserId(id, user.getId())) {
            throw new RuntimeException("You are not a member of this group");
        }
        return expenseRepo.findByGroupId(id).stream()
                .map(this::mapToResponse).toList();
    }

    public List<BalanceResponse> seeExpenses(Long id, String email) {
        UserEntity user = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!groupMemberRepository.existsByGroupIdAndUserId(id, user.getId())) {
            throw new RuntimeException("You are not a member of this group");
        }

        // get all unsettled splits for this group
        List<ExpenseSplit> splits = expenseSplitRepo
                .findByExpense_GroupIdAndSettled(id, false);

        // net[debtor][creditor] = amount owed
        Map<Long, Map<Long, BigDecimal>> net = new HashMap<>();

        for (ExpenseSplit split : splits) {
            Long debtorId = split.getUser().getId();
            Long creditorId = split.getExpense().getPaidBy().getId();

            if (debtorId.equals(creditorId)) continue; // payer doesn't owe themselves

            net.computeIfAbsent(debtorId, k -> new HashMap<>())
                    .merge(creditorId, split.getAmountOwed(), BigDecimal::add);
        }

        List<BalanceResponse> balances = new ArrayList<>();
        for (Map.Entry<Long, Map<Long, BigDecimal>> debtor : net.entrySet()) {
            UserEntity debtorUser = userRepo.findById(debtor.getKey()).orElseThrow();
            for (Map.Entry<Long, BigDecimal> creditor : debtor.getValue().entrySet()) {
                UserEntity creditorUser = userRepo.findById(creditor.getKey()).orElseThrow();
                BalanceResponse b = new BalanceResponse();
                b.setFromUserId(debtorUser.getId());
                b.setFromUsername(debtorUser.getUsername());
                b.setToUserId(creditorUser.getId());
                b.setToUsername(creditorUser.getUsername());
                b.setAmount(creditor.getValue());
                balances.add(b);
            }
        }
        return balances;
    }

    @Transactional
    public String settleExpense(SettlementRequest req, String email) {
        UserEntity payer = userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity payee = userRepo.findById(req.getToUserId())
                .orElseThrow(() -> new RuntimeException("Payee not found"));

        // ✅ Prevent settling with yourself
        if (payer.getId().equals(payee.getId())) {
            throw new RuntimeException("You cannot settle a payment with yourself");
        }

        // Only settle splits up to the amount paid
        List<ExpenseSplit> splits = expenseSplitRepo
                .findByUserIdAndExpense_GroupIdAndSettled(payer.getId(), req.getGroupId(), false);

        if (splits.isEmpty()) {
            throw new RuntimeException("You have no outstanding balance in this group");
        }

        BigDecimal remaining = req.getAmount();
        for (ExpenseSplit split : splits) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            if (split.getAmountOwed().compareTo(remaining) <= 0) {
                split.setSettled(true);
                remaining = remaining.subtract(split.getAmountOwed());
            } else {
                // partial settlement — split the split
                split.setAmountOwed(split.getAmountOwed().subtract(remaining));
                remaining = BigDecimal.ZERO;
            }
        }
        expenseSplitRepo.saveAll(splits);

        // eSewa v2
        String amount = req.getAmount().setScale(2, RoundingMode.HALF_UP).toString();
        String txUuid = "SPLIT-" + payer.getId() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        String productCode = "EPAYTEST";
        String secretKey = "8gBm/:&EnhH.1/q";
        String signedFields = "total_amount=" + amount + ",transaction_uuid=" + txUuid + ",product_code=" + productCode;
        String signature = generateHmacSHA256(signedFields, secretKey);

        return "{" +
                "\"amount\":\"" + amount + "\"," +
                "\"total_amount\":\"" + amount + "\"," +
                "\"transaction_uuid\":\"" + txUuid + "\"," +
                "\"product_code\":\"" + productCode + "\"," +
                "\"signature\":\"" + signature + "\"," +
                "\"success_url\":\"http://localhost:8080/success\"," +
                "\"failure_url\":\"http://localhost:8080/failure\"" +
                "}";
    }

    private String generateHmacSHA256(String data, String secret) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    secret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        ExpenseResponse r = new ExpenseResponse();
        r.setId(expense.getId());
        r.setAmount(expense.getAmount());
        r.setDescription(expense.getDescription());
        r.setCreatedAt(expense.getCreatedAt());
        r.setPaidBy(expense.getPaidBy().getUsername());
        r.setGroupId(expense.getGroup().getId());
        r.setCategory(expense.getCategory());
        return r;
    }
}