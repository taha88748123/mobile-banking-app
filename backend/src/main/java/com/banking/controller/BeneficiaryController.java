package com.banking.controller;

import com.banking.dto.AddBeneficiaryRequest;
import com.banking.dto.ApiResponse;
import com.banking.dto.BeneficiaryDto;
import com.banking.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints proteges pour la gestion des beneficiaires.
 */
@RestController
@RequestMapping("/api/beneficiaries")
@CrossOrigin(originPatterns = "*")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryDto>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(beneficiaryService.list(email));
    }

    @PostMapping
    public ResponseEntity<BeneficiaryDto> add(@AuthenticationPrincipal String email,
                                              @Valid @RequestBody AddBeneficiaryRequest request) {
        return ResponseEntity.ok(beneficiaryService.add(email, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@AuthenticationPrincipal String email,
                                              @PathVariable Long id) {
        beneficiaryService.delete(email, id);
        return ResponseEntity.ok(ApiResponse.ok("Beneficiaire supprime"));
    }
}
