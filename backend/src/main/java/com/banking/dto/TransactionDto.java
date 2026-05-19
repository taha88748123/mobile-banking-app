package com.banking.dto;

import com.banking.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime timestamp;

    public static TransactionDto fromEntity(Transaction t) {
        return TransactionDto.builder()
                .id(t.getId())
                .fromAccount(t.getFromAccount())
                .toAccount(t.getToAccount())
                .amount(t.getAmount())
                .type(t.getType().name())
                .status(t.getStatus().name())
                .description(t.getDescription())
                .timestamp(t.getTimestamp())
                .build();
    }
}
