package com.banking.service;

import com.banking.dto.AccountInfoResponse;
import com.banking.entity.Account;
import com.banking.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable pour cet utilisateur"));
    }

    public AccountInfoResponse getAccountInfo(String email) {
        Account acc = getAccountByEmail(email);
        return AccountInfoResponse.builder()
                .accountNumber(acc.getAccountNumber())
                .balance(acc.getBalance())
                .accountType(acc.getAccountType().name())
                .fullName(acc.getUser().getFullName())
                .email(acc.getUser().getEmail())
                .build();
    }
}
