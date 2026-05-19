package com.banking.app.models;

import java.math.BigDecimal;

public class TransferRequest {
    public String toAccountNumber;
    public BigDecimal amount;
    public String description;

    public TransferRequest(String toAccountNumber, BigDecimal amount, String description) {
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.description = description;
    }
}
