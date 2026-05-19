package com.banking.service;

import com.banking.dto.StatisticsResponse;
import com.banking.dto.TransactionDto;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Calcul des statistiques mensuelles et globales pour un utilisateur.
 */
@Service
public class StatisticsService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public StatisticsService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public StatisticsResponse compute(String email) {
        Account acc = accountService.getAccountByEmail(email);
        String accNumber = acc.getAccountNumber();
        List<Transaction> all = transactionRepository.findByAccountNumber(accNumber);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal monthIncome = BigDecimal.ZERO;
        BigDecimal monthExpenses = BigDecimal.ZERO;
        BigDecimal largestIncome = BigDecimal.ZERO;
        BigDecimal largestExpense = BigDecimal.ZERO;
        int incomingCount = 0;
        int outgoingCount = 0;

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        for (Transaction t : all) {
            if (t.getStatus() != Transaction.TransactionStatus.SUCCESS) continue;
            boolean incoming = accNumber.equals(t.getToAccount());
            BigDecimal amount = t.getAmount();
            boolean isMonth = t.getTimestamp() != null && t.getTimestamp().isAfter(startOfMonth);

            if (incoming) {
                totalIncome = totalIncome.add(amount);
                if (isMonth) monthIncome = monthIncome.add(amount);
                if (amount.compareTo(largestIncome) > 0) largestIncome = amount;
                incomingCount++;
            } else {
                totalExpenses = totalExpenses.add(amount);
                if (isMonth) monthExpenses = monthExpenses.add(amount);
                if (amount.compareTo(largestExpense) > 0) largestExpense = amount;
                outgoingCount++;
            }
        }

        int total = incomingCount + outgoingCount;
        BigDecimal average = total == 0 ? BigDecimal.ZERO
                : totalIncome.add(totalExpenses).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        List<TransactionDto> topExpenses = all.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.SUCCESS
                        && !accNumber.equals(t.getToAccount()))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .limit(5)
                .map(TransactionDto::fromEntity)
                .toList();

        List<TransactionDto> topIncomes = all.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.SUCCESS
                        && accNumber.equals(t.getToAccount()))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .limit(5)
                .map(TransactionDto::fromEntity)
                .toList();

        return StatisticsResponse.builder()
                .currentBalance(acc.getBalance())
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netCashflow(totalIncome.subtract(totalExpenses))
                .transactionCount(total)
                .incomingCount(incomingCount)
                .outgoingCount(outgoingCount)
                .averageTransaction(average)
                .largestIncome(largestIncome)
                .largestExpense(largestExpense)
                .topExpenses(topExpenses)
                .topIncomes(topIncomes)
                .monthDays(LocalDate.now().getDayOfMonth())
                .monthIncome(monthIncome)
                .monthExpenses(monthExpenses)
                .build();
    }
}
