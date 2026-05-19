package com.banking.app.models;

public class AddBeneficiaryRequest {
    public String label;
    public String accountNumber;

    public AddBeneficiaryRequest(String label, String accountNumber) {
        this.label = label;
        this.accountNumber = accountNumber;
    }
}
