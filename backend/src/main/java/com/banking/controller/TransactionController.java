package com.banking.controller;

import com.banking.dto.DepositRequest;
import com.banking.dto.TransactionDto;
import com.banking.dto.TransferRequest;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints proteges pour les transactions bancaires.
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(originPatterns = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDto> transfer(@AuthenticationPrincipal String email,
                                                   @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(email, request));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> deposit(@AuthenticationPrincipal String email,
                                                  @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(transactionService.deposit(email, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionDto>> history(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(transactionService.history(email));
    }
}
