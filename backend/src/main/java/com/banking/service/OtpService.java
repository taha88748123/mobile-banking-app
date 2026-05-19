package com.banking.service;

import com.banking.entity.OtpToken;
import com.banking.repository.OtpTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service de gestion des codes OTP.
 */
@Service
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int EXPIRY_MINUTES = 10;

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;

    public OtpService(OtpTokenRepository otpTokenRepository, EmailService emailService) {
        this.otpTokenRepository = otpTokenRepository;
        this.emailService = emailService;
    }

    /**
     * Genere un code OTP a 6 chiffres et l'envoie par email.
     */
    @Transactional
    public String generateAndSendOtp(String email) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        OtpToken token = OtpToken.builder()
                .email(email)
                .otpCode(code)
                .expiryDate(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES))
                .verified(false)
                .build();
        otpTokenRepository.save(token);
        emailService.sendOtpEmail(email, code);
        return code;
    }

    /**
     * Verifie le code OTP fourni.
     */
    @Transactional
    public boolean verifyOtp(String email, String code) {
        Optional<OtpToken> latest = otpTokenRepository.findTopByEmailOrderByIdDesc(email);
        if (latest.isEmpty()) {
            return false;
        }
        OtpToken token = latest.get();
        if (token.isVerified() || token.isExpired()) {
            return false;
        }
        if (!token.getOtpCode().equals(code)) {
            return false;
        }
        token.setVerified(true);
        otpTokenRepository.save(token);
        return true;
    }
}
