package com.banking.config;

import com.banking.entity.Account;
import com.banking.entity.Beneficiary;
import com.banking.entity.Transaction;
import com.banking.entity.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.BeneficiaryRepository;
import com.banking.repository.TransactionRepository;
import com.banking.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Charge des donnees de test au demarrage :
 * - 3 comptes utilisateurs pre-actives
 * - une trentaine de transactions fictives realistes sur ~6 semaines
 * - bénéficiaires croises entre les 3 comptes
 */
@Component
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,
                      AccountRepository accountRepository,
                      TransactionRepository transactionRepository,
                      BeneficiaryRepository beneficiaryRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void loadDemoData() {
        if (userRepository.existsByEmail("ahmed.tazi@bank.com")) {
            log.info("Donnees de demo deja chargees -- skip.");
            return;
        }

        Account ahmed = createUser("ahmed.tazi@bank.com", "Ahmed Tazi", "0612345601",
                "BANK-10000001", new BigDecimal("5000.00"));
        Account fatima = createUser("fatima.ouali@bank.com", "Fatima Ouali", "0612345602",
                "BANK-10000002", new BigDecimal("3000.00"));
        Account karim = createUser("karim.benani@bank.com", "Karim Benani", "0612345603",
                "BANK-10000003", new BigDecimal("8500.00"));

        // Aliases d'ancien compte de test pour compat retro
        createUser("test1@bank.com", "Test One", "0600000001",
                "BANK-90000001", new BigDecimal("2000.00"));
        createUser("test2@bank.com", "Test Two", "0600000002",
                "BANK-90000002", new BigDecimal("1500.00"));

        seedTransactions(ahmed, fatima, karim);
        seedBeneficiaries(ahmed.getUser(), fatima, karim);
        seedBeneficiaries(fatima.getUser(), ahmed, karim);
        seedBeneficiaries(karim.getUser(), ahmed, fatima);

        log.info("===========================================================");
        log.info("Donnees de demo chargees - 3 comptes principaux :");
        log.info("   ahmed.tazi@bank.com    / password123  (5000 MAD)");
        log.info("   fatima.ouali@bank.com  / password123  (3000 MAD)");
        log.info("   karim.benani@bank.com  / password123  (8500 MAD)");
        log.info("   + test1@bank.com / test2@bank.com (legacy)");
        log.info("===========================================================");
    }

    private Account createUser(String email, String name, String phone, String acc, BigDecimal balance) {
        User user = User.builder()
                .fullName(name).email(email).phone(phone).enabled(true)
                .password(passwordEncoder.encode("password123"))
                .build();
        user = userRepository.save(user);
        Account account = Account.builder()
                .accountNumber(acc).balance(balance)
                .accountType(Account.AccountType.CHECKING).user(user)
                .build();
        return accountRepository.save(account);
    }

    private void seedTransactions(Account ahmed, Account fatima, Account karim) {
        LocalDateTime now = LocalDateTime.now();

        // Mois courant
        tx(ahmed.getAccountNumber(), fatima.getAccountNumber(), "850.00", "Loyer mai",     daysAgo(2));
        tx(karim.getAccountNumber(), ahmed.getAccountNumber(), "1500.00", "Salaire freelance", daysAgo(3));
        tx(ahmed.getAccountNumber(), karim.getAccountNumber(), "320.50", "Restaurant Casablanca", daysAgo(4));
        tx(fatima.getAccountNumber(), ahmed.getAccountNumber(), "450.00", "Remboursement vacances", daysAgo(5));
        tx(ahmed.getAccountNumber(), "BANK-EXTERN01", "180.75", "Facture EDF", daysAgo(6));
        deposit(ahmed.getAccountNumber(), "1200.00", "Recharge compte", daysAgo(7));
        tx(ahmed.getAccountNumber(), fatima.getAccountNumber(), "75.00", "Cafe + dej equipe", daysAgo(8));
        tx(karim.getAccountNumber(), ahmed.getAccountNumber(), "2300.00", "Prime projet Q1", daysAgo(9));
        tx(ahmed.getAccountNumber(), "BANK-EXTERN02", "560.00", "Courses Marjane", daysAgo(10));
        tx(ahmed.getAccountNumber(), karim.getAccountNumber(), "150.00", "Cadeau anniversaire", daysAgo(11));
        tx(fatima.getAccountNumber(), karim.getAccountNumber(), "220.00", "Internet IAM", daysAgo(12));
        tx(ahmed.getAccountNumber(), "BANK-EXTERN03", "99.99", "Abonnement Netflix", daysAgo(13));
        deposit(fatima.getAccountNumber(), "3500.00", "Salaire avril", daysAgo(14));

        // Mois precedent
        tx(ahmed.getAccountNumber(), fatima.getAccountNumber(), "850.00", "Loyer avril", daysAgo(32));
        tx(karim.getAccountNumber(), ahmed.getAccountNumber(), "1500.00", "Salaire freelance", daysAgo(33));
        tx(ahmed.getAccountNumber(), "BANK-EXTERN02", "612.40", "Courses Carrefour", daysAgo(35));
        tx(ahmed.getAccountNumber(), karim.getAccountNumber(), "200.00", "Sortie ciné", daysAgo(37));
        tx(fatima.getAccountNumber(), ahmed.getAccountNumber(), "120.00", "Remboursement Uber", daysAgo(38));
        tx(ahmed.getAccountNumber(), "BANK-EXTERN03", "99.99", "Abonnement Netflix", daysAgo(43));
        tx(ahmed.getAccountNumber(), "BANK-EXTERN04", "260.00", "Facture eau", daysAgo(44));
        deposit(karim.getAccountNumber(), "4500.00", "Mission consultant", daysAgo(45));
        tx(karim.getAccountNumber(), fatima.getAccountNumber(), "180.00", "Pizza soiree", daysAgo(46));
        tx(ahmed.getAccountNumber(), fatima.getAccountNumber(), "60.00", "Taxi aeroport", daysAgo(48));
        tx(fatima.getAccountNumber(), karim.getAccountNumber(), "90.00", "Cadeau commun", daysAgo(50));

        // Echec (solde insuffisant simule)
        Transaction failed = Transaction.builder()
                .fromAccount(fatima.getAccountNumber())
                .toAccount(ahmed.getAccountNumber())
                .amount(new BigDecimal("9500.00"))
                .type(Transaction.TransactionType.TRANSFER)
                .status(Transaction.TransactionStatus.FAILED)
                .description("Solde insuffisant")
                .timestamp(daysAgo(20))
                .build();
        transactionRepository.save(failed);

        // Withdraw simule
        Transaction withdraw = Transaction.builder()
                .fromAccount(karim.getAccountNumber())
                .toAccount("ATM-CASA-CENTER")
                .amount(new BigDecimal("400.00"))
                .type(Transaction.TransactionType.WITHDRAW)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description("Retrait DAB Casablanca")
                .timestamp(daysAgo(15))
                .build();
        transactionRepository.save(withdraw);
    }

    private void tx(String from, String to, String amount, String description, LocalDateTime when) {
        Transaction t = Transaction.builder()
                .fromAccount(from).toAccount(to)
                .amount(new BigDecimal(amount))
                .type(Transaction.TransactionType.TRANSFER)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description(description)
                .timestamp(when)
                .build();
        transactionRepository.save(t);
    }

    private void deposit(String account, String amount, String description, LocalDateTime when) {
        Transaction t = Transaction.builder()
                .fromAccount("EXTERNAL").toAccount(account)
                .amount(new BigDecimal(amount))
                .type(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description(description)
                .timestamp(when)
                .build();
        transactionRepository.save(t);
    }

    private LocalDateTime daysAgo(int days) {
        return LocalDateTime.now().minusDays(days).withHour(10).withMinute(30);
    }

    private void seedBeneficiaries(User owner, Account a, Account b) {
        addBeneficiary(owner, a, labelFor(a));
        addBeneficiary(owner, b, labelFor(b));
    }

    private void addBeneficiary(User owner, Account target, String label) {
        Beneficiary b = Beneficiary.builder()
                .user(owner)
                .label(label)
                .accountNumber(target.getAccountNumber())
                .beneficiaryName(target.getUser().getFullName())
                .build();
        beneficiaryRepository.save(b);
    }

    private String labelFor(Account a) {
        String name = a.getUser().getFullName();
        return name.split(" ")[0];
    }
}
