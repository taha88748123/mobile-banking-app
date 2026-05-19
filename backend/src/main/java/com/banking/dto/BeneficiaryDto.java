package com.banking.dto;

import com.banking.entity.Beneficiary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryDto {
    private Long id;
    private String label;
    private String accountNumber;
    private String beneficiaryName;
    private LocalDateTime createdAt;

    public static BeneficiaryDto fromEntity(Beneficiary b) {
        return BeneficiaryDto.builder()
                .id(b.getId())
                .label(b.getLabel())
                .accountNumber(b.getAccountNumber())
                .beneficiaryName(b.getBeneficiaryName())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
