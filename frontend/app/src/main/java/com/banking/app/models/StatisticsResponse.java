package com.banking.app.models;

import java.math.BigDecimal;
import java.util.List;

public class StatisticsResponse {
    public BigDecimal currentBalance;
    public BigDecimal totalIncome;
    public BigDecimal totalExpenses;
    public BigDecimal netCashflow;
    public int transactionCount;
    public int incomingCount;
    public int outgoingCount;
    public BigDecimal averageTransaction;
    public BigDecimal largestIncome;
    public BigDecimal largestExpense;
    public List<Transaction> topExpenses;
    public List<Transaction> topIncomes;
    public int monthDays;
    public BigDecimal monthIncome;
    public BigDecimal monthExpenses;
}
