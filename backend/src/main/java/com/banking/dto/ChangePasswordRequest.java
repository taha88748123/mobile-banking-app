package com.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Mot de passe actuel requis")
    private String currentPassword;

    @NotBlank
    @Size(min = 6, message = "Le nouveau mot de passe doit contenir au moins 6 caracteres")
    private String newPassword;
}
