package com.banking.controller;

import com.banking.dto.ApiResponse;
import com.banking.dto.ChangePasswordRequest;
import com.banking.dto.ProfileResponse;
import com.banking.dto.UpdateProfileRequest;
import com.banking.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints proteges pour le profil utilisateur.
 */
@RestController
@RequestMapping("/api/profile")
@CrossOrigin(originPatterns = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> get(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(profileService.getProfile(email));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> update(@AuthenticationPrincipal String email,
                                                  @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(email, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@AuthenticationPrincipal String email,
                                                      @Valid @RequestBody ChangePasswordRequest request) {
        profileService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.ok("Mot de passe modifie avec succes"));
    }
}
