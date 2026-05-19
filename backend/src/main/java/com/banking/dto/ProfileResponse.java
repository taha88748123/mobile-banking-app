package com.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private String accountNumber;
    private String accountType;
}
