package com.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoResponse {
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String fullName;
    private String email;
}
