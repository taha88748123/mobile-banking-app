package com.banking.service;

import com.banking.dto.AddBeneficiaryRequest;
import com.banking.dto.BeneficiaryDto;
import com.banking.entity.Account;
import com.banking.entity.Beneficiary;
import com.banking.entity.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.BeneficiaryRepository;
import com.banking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des beneficiaires enregistres par l'utilisateur.
 */
@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository,
                              UserRepository userRepository,
                              AccountRepository accountRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public List<BeneficiaryDto> list(String email) {
        return beneficiaryRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream().map(BeneficiaryDto::fromEntity).toList();
    }

    @Transactional
    public BeneficiaryDto add(String email, AddBeneficiaryRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Account ownAccount = accountRepository.findByUserEmail(email).orElse(null);
        if (ownAccount != null && ownAccount.getAccountNumber().equals(request.getAccountNumber())) {
            throw new IllegalArgumentException("Impossible d'ajouter son propre compte comme beneficiaire");
        }
        if (beneficiaryRepository.existsByUserEmailAndAccountNumber(email, request.getAccountNumber())) {
            throw new IllegalArgumentException("Ce beneficiaire existe deja");
        }
        Account dest = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Le compte beneficiaire n'existe pas"));

        Beneficiary entity = Beneficiary.builder()
                .label(request.getLabel())
                .accountNumber(request.getAccountNumber())
                .beneficiaryName(dest.getUser().getFullName())
                .user(user)
                .build();
        return BeneficiaryDto.fromEntity(beneficiaryRepository.save(entity));
    }

    @Transactional
    public void delete(String email, Long id) {
        Beneficiary entity = beneficiaryRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiaire introuvable"));
        beneficiaryRepository.delete(entity);
    }
}
