package com.banking.app.models;

import java.math.BigDecimal;

public class DepositRequest {
    public BigDecimal amount;
    public String description;

    public DepositRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }
}
