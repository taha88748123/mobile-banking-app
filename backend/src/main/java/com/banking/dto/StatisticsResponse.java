package com.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {
    private BigDecimal currentBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netCashflow;
    private int transactionCount;
    private int incomingCount;
    private int outgoingCount;
    private BigDecimal averageTransaction;
    private BigDecimal largestIncome;
    private BigDecimal largestExpense;
    private List<TransactionDto> topExpenses;
    private List<TransactionDto> topIncomes;
    private int monthDays;
    private BigDecimal monthIncome;
    private BigDecimal monthExpenses;
}
