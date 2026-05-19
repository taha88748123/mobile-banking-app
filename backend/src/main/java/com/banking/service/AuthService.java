package com.banking.service;

import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.OtpRequest;
import com.banking.dto.SignupRequest;
import com.banking.entity.Account;
import com.banking.entity.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import com.banking.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;

/**
 * Service d'authentification : signup, verification OTP, login.
 */
@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       AccountRepository accountRepository,
                       OtpService otpService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Inscription : cree l'utilisateur (desactive) et envoie un OTP par email.
     */
    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un compte existe deja avec cet email");
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .enabled(false)
                .build();
        userRepository.save(user);
        otpService.generateAndSendOtp(request.getEmail());
    }

    /**
     * Verifie l'OTP, active le compte et cree le compte bancaire associe.
     */
    @Transactional
    public void verifyOtp(OtpRequest request) {
        boolean ok = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (!ok) {
            throw new IllegalArgumentException("Code OTP invalide ou expire");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        user.setEnabled(true);
        userRepository.save(user);

        if (user.getAccount() == null) {
            Account account = Account.builder()
                    .accountNumber(generateAccountNumber())
                    .balance(new BigDecimal("1000.00"))
                    .accountType(Account.AccountType.CHECKING)
                    .user(user)
                    .build();
            accountRepository.save(account);
        }
    }

    /**
     * Renvoie un nouveau code OTP a l'utilisateur.
     */
    public void resendOtp(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Aucun compte associe a cet email");
        }
        otpService.generateAndSendOtp(email);
    }

    /**
     * Authentifie l'utilisateur et retourne un JWT.
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Compte non active. Veuillez verifier l'OTP envoye par email.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        String accountNumber = user.getAccount() != null ? user.getAccount().getAccountNumber() : null;
        return LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .accountNumber(accountNumber)
                .message("Connexion reussie")
                .build();
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "BANK-" + String.format("%08d", RANDOM.nextInt(100_000_000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
