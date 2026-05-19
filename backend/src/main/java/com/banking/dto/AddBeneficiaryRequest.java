package com.banking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddBeneficiaryRequest {

    @NotBlank(message = "Le libelle est obligatoire")
    private String label;

    @NotBlank(message = "Le numero de compte est obligatoire")
    private String accountNumber;
}
