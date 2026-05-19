package com.banking.service;

import com.banking.dto.DepositRequest;
import com.banking.dto.TransactionDto;
import com.banking.dto.TransferRequest;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de gestion des transactions bancaires.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    /**
     * Effectue un virement entre deux comptes.
     */
    @Transactional
    public TransactionDto transfer(String userEmail, TransferRequest request) {
        Account from = accountService.getAccountByEmail(userEmail);
        Account to = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Compte destinataire introuvable"));

        if (from.getAccountNumber().equals(to.getAccountNumber())) {
            throw new IllegalArgumentException("Impossible de virer sur son propre compte");
        }
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            Transaction failed = Transaction.builder()
                    .fromAccount(from.getAccountNumber())
                    .toAccount(to.getAccountNumber())
                    .amount(request.getAmount())
                    .type(Transaction.TransactionType.TRANSFER)
                    .status(Transaction.TransactionStatus.FAILED)
                    .description("Solde insuffisant")
                    .build();
            transactionRepository.save(failed);
            throw new IllegalArgumentException("Solde insuffisant");
        }
        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = Transaction.builder()
                .fromAccount(from.getAccountNumber())
                .toAccount(to.getAccountNumber())
                .amount(request.getAmount())
                .type(Transaction.TransactionType.TRANSFER)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description(request.getDescription() != null ? request.getDescription() : "Virement")
                .build();
        return TransactionDto.fromEntity(transactionRepository.save(tx));
    }

    /**
     * Effectue un depot simule sur le compte de l'utilisateur.
     */
    @Transactional
    public TransactionDto deposit(String userEmail, DepositRequest request) {
        Account acc = accountService.getAccountByEmail(userEmail);
        acc.setBalance(acc.getBalance().add(request.getAmount()));
        accountRepository.save(acc);
        Transaction tx = Transaction.builder()
                .fromAccount("EXTERNAL")
                .toAccount(acc.getAccountNumber())
                .amount(request.getAmount())
                .type(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description(request.getDescription() != null ? request.getDescription() : "Depot")
                .build();
        return TransactionDto.fromEntity(transactionRepository.save(tx));
    }

    /**
     * Retourne l'historique des transactions de l'utilisateur connecte.
     */
    public List<TransactionDto> history(String userEmail) {
        Account acc = accountService.getAccountByEmail(userEmail);
        return transactionRepository.findByAccountNumber(acc.getAccountNumber())
                .stream()
                .map(TransactionDto::fromEntity)
                .toList();
    }
}
