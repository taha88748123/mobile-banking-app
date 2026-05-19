# Backend Banking App - Spring Boot

API REST securisee pour l'application bancaire mobile.

## Lancement rapide

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Le serveur demarre sur `http://localhost:8080`.

Console H2 (BD en memoire) : `http://localhost:8080/h2-console` (JDBC URL : `jdbc:h2:mem:bankdb`, user `sa`, sans mot de passe).

## Endpoints principaux

| Methode | URL | Description |
|---------|-----|-------------|
| POST | `/api/auth/signup` | Inscription (envoie OTP par email) |
| POST | `/api/auth/verify-otp` | Validation du code OTP |
| POST | `/api/auth/resend-otp` | Renvoi du code |
| POST | `/api/auth/login` | Connexion (retourne JWT) |
| GET | `/api/account/info` | Infos compte (proteges JWT) |
| GET | `/api/account/balance` | Solde |
| POST | `/api/transactions/transfer` | Virement |
| POST | `/api/transactions/deposit` | Depot simule |
| GET | `/api/transactions/history` | Historique |

## Mode email simule

Par defaut `app.email.simulate=true`. Les codes OTP sont affiches dans la console du backend, sans configurer Gmail. Pour envoyer de vrais emails, definir `app.email.simulate=false` et renseigner `spring.mail.username` / `spring.mail.password` dans `application.properties` (App Password Gmail).

## Comptes de test

- `test1@bank.com` / `password123` (solde 5000, compte BANK-10000001)
- `test2@bank.com` / `password123` (solde 3000, compte BANK-10000002)
