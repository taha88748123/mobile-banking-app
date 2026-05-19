package com.banking.controller;

import com.banking.dto.ApiResponse;
import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.OtpRequest;
import com.banking.dto.SignupRequest;
import com.banking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints d'authentification (signup, OTP, login).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.ok(
                "Inscription reussie. Un code OTP a ete envoye a " + request.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody OtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.ok("Compte active avec succes"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        authService.resendOtp(email);
        return ResponseEntity.ok(ApiResponse.ok("Nouveau code OTP envoye"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
