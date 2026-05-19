package com.banking.repository;

import com.banking.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByUserEmailOrderByCreatedAtDesc(String email);
    Optional<Beneficiary> findByIdAndUserEmail(Long id, String email);
    boolean existsByUserEmailAndAccountNumber(String email, String accountNumber);
}
