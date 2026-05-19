package com.banking.app.models;

import java.math.BigDecimal;

public class Transaction {
    public Long id;
    public String fromAccount;
    public String toAccount;
    public BigDecimal amount;
    public String type;
    public String status;
    public String description;
    public String timestamp;
}
