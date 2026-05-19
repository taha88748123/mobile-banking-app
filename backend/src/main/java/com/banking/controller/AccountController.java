package com.banking.controller;

import com.banking.dto.AccountInfoResponse;
import com.banking.entity.Account;
import com.banking.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Endpoints proteges concernant le compte bancaire.
 */
@RestController
@RequestMapping("/api/account")
@CrossOrigin(originPatterns = "*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/info")
    public ResponseEntity<AccountInfoResponse> info(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(accountService.getAccountInfo(email));
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> balance(@AuthenticationPrincipal String email) {
        Account acc = accountService.getAccountByEmail(email);
        return ResponseEntity.ok(Map.of("balance", acc.getBalance()));
    }
}
