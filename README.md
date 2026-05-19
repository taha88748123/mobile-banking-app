# Banking App - Application Bancaire Mobile Full-Stack

Projet d'equipe pour le module **Developpement Mobile** (2025/2026)

**Equipe :** NACHIT TAHA, NOUREDDINE EZZYN, YASSIR RAHHIL, REDA EL ANSARY

---

## Sommaire
1. [Architecture](#architecture)
2. [Prerequis](#prerequis)
3. [Lancer le backend Spring Boot](#lancer-le-backend-spring-boot)
4. [Lancer le frontend Android sur un telephone virtuel (emulateur)](#lancer-le-frontend-android-sur-un-telephone-virtuel-emulateur)
5. [Lancer sur un telephone physique](#lancer-sur-un-telephone-physique)
6. [Comptes de test pre-charges](#comptes-de-test-pre-charges)
7. [Tester le backend avec Postman / curl](#tester-le-backend-avec-postman--curl)
8. [Compiler le rapport LaTeX et la presentation](#compiler-le-rapport-latex-et-la-presentation)
9. [Structure du projet](#structure-du-projet)

---

## Ecrans de l'application (8 au total)
1. **SplashActivity** - logo + aiguillage automatique
2. **SignupActivity** - inscription
3. **OtpVerificationActivity** - validation du code OTP recu par email
4. **LoginActivity** - connexion JWT
5. **DashboardActivity** - solde, virement, depot, historique, raccourcis Profil/Beneficiaires/Statistiques
6. **ProfileActivity** - modifier nom/telephone + changer mot de passe
7. **BeneficiariesActivity** - liste / ajout / suppression / virement rapide
8. **StatisticsActivity** - revenus/depenses, repartition visuelle, top transactions, mois en cours

## Architecture
```
Client Android (Java + Retrofit)
        |
        | HTTP / JSON + JWT Bearer
        v
Backend Spring Boot (REST)
        |
        | JPA
        v
Base de donnees H2 (dev) / MySQL (prod)
```

L'inscription envoie un OTP a 6 chiffres par email (JavaMail). Une fois valide, le compte est active et un compte bancaire avec solde initial de 1000 MAD est cree. Toutes les requetes metier sont protegees par un JWT (24h).

---

## Prerequis
| Outil | Version | Lien |
|-------|---------|------|
| Java JDK | 17+ | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org/download.cgi |
| Android Studio | Hedgehog ou +recent | https://developer.android.com/studio |
| Gradle | fourni par Android Studio | -- |
| Compte Gmail + App Password | facultatif (mode simulation par defaut) | https://myaccount.google.com/apppasswords |
| Python 3 + python-pptx | seulement si vous voulez regenerer la pres | `pip install python-pptx` |
| TeX Live / MikTeX | seulement pour compiler le rapport | -- |

---

## Lancer le backend Spring Boot

### Etape 1 - Configurer l'envoi des emails
Les credentials Gmail sont deja renseignes dans `application.properties` (tnnachit@gmail.com + App Password). Mode reel actif (`app.email.simulate=false`).

Pour utiliser un autre compte Gmail, definir les variables d'environnement :
```powershell
$env:GMAIL_USER = "votre_email@gmail.com"
$env:GMAIL_APP_PASSWORD = "votre_app_password_16_chars"
```

Pour revenir au mode simulation (code OTP affiche dans la console), mettre `app.email.simulate=true`.

### Etape 2 - Build & run
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Sous Windows / PowerShell :
```powershell
cd backend
mvn clean install
mvn spring-boot:run
```

Le serveur ecoute sur **http://localhost:8080**.

Console base H2 : http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:bankdb`, user `sa`, mot de passe vide).

### Etape 3 - Verifier qu'il repond
Dans un autre terminal :
```bash
curl http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d "{\"email\":\"test1@bank.com\",\"password\":\"password123\"}"
```
Vous devez recevoir un JSON contenant un `token` JWT.

---

## Lancer le frontend Android sur un telephone virtuel (emulateur)

### Etape 1 - Ouvrir le projet
1. Lancer **Android Studio**.
2. `File > Open` puis selectionner le dossier `BankingApp/frontend`.
3. Attendre la fin de la synchronisation Gradle (1-3 minutes la premiere fois). Si Android Studio propose de mettre a jour Gradle / Android plugin, accepter.

### Etape 2 - Creer un telephone virtuel (Android Virtual Device)
1. Ouvrir le **Device Manager** : `Tools > Device Manager` (ou icone telephone dans la barre laterale).
2. Cliquer **Create Device**.
3. Choisir un modele moderne, par exemple **Pixel 6** ou **Pixel 7** -> Next.
4. Choisir un system image API **24 ou superieur** (recommande : **API 34 - Android 14**). Telecharger si necessaire (download : ~1.5 Go la premiere fois).
5. Cliquer Next puis Finish.

### Etape 3 - Demarrer l'emulateur
1. Dans le Device Manager, cliquer le bouton **Play** (triangle) a cote du device cree.
2. Patienter jusqu'a ce que l'ecran d'accueil Android s'affiche.

### Etape 4 - **Avant de lancer l'app, demarrer le backend** (etape precedente).

### Etape 5 - Run l'app
1. Dans Android Studio, en haut, selectionner la configuration **app** et le device emulator.
2. Cliquer le bouton **Run** (triangle vert) ou `Shift + F10`.
3. L'app se construit puis s'installe automatiquement sur l'emulateur.
4. Sur l'emulateur, le splash Banking App apparait puis l'ecran de connexion.

L'app utilise l'URL **`http://10.0.2.2:8080/api/`** qui, depuis un emulateur Android, pointe vers le `localhost` de votre PC -> donc vers votre backend. Aucune configuration reseau supplementaire.

### Etape 6 - Tester un parcours complet
- Cliquer "Pas de compte ? S'inscrire".
- Saisir nom, email, telephone, mot de passe (>= 6 caracteres) et confirmer.
- Cliquer "S'inscrire" : un code OTP est genere.
  - Mode **simulation** (par defaut) : regarder la **console du backend**, le code s'affiche entre des barres `=====`.
  - Mode **email reel** : le code arrive dans la boite mail.
- Saisir le code dans l'ecran OTP -> "Verifier".
- Se connecter -> le **Dashboard** s'ouvre avec solde 1000 MAD et numero de compte `BANK-XXXXXXXX`.
- Tester un virement vers `BANK-10000001` (compte de test) avec un petit montant.

---

## Lancer sur un telephone physique
1. Activer le mode developpeur sur le telephone (`Reglages > A propos > 7 taps sur Build Number`).
2. Activer **Debogage USB**.
3. Brancher en USB et autoriser le PC.
4. Le telephone doit apparaitre dans le selecteur d'Android Studio.
5. **Important** : `10.0.2.2` ne fonctionne pas sur un telephone physique. Il faut modifier l'URL :
   - Recuperer l'IP locale du PC : `ipconfig` (PowerShell) -> note l'IPv4 de votre Wi-Fi (ex : `192.168.1.10`).
   - Editer `frontend/app/src/main/java/com/banking/app/network/ApiConfig.java` :
     ```java
     public static final String BASE_URL = "http://192.168.1.10:8080/api/";
     ```
   - PC et telephone doivent etre sur le **meme WiFi**.
   - Verifier que le pare-feu Windows autorise les connexions entrantes sur le port 8080 (ou desactiver temporairement le firewall pour le test).
6. Run.

---

## Comptes de test pre-charges
Le `DataLoader` cree au demarrage 3 comptes principaux + 2 comptes legacy :

| Email | Mot de passe | Numero de compte | Solde initial | Profil |
|-------|--------------|------------------|---------------|--------|
| **ahmed.tazi@bank.com** | password123 | BANK-10000001 | 5 000,00 MAD | Compte principal, beaucoup de transactions |
| **fatima.ouali@bank.com** | password123 | BANK-10000002 | 3 000,00 MAD | Recoit son loyer mensuel d'Ahmed |
| **karim.benani@bank.com** | password123 | BANK-10000003 | 8 500,00 MAD | Verse des salaires freelance |
| test1@bank.com | password123 | BANK-90000001 | 2 000,00 MAD | Compte legacy (vide) |
| test2@bank.com | password123 | BANK-90000002 | 1 500,00 MAD | Compte legacy (vide) |

### Donnees fictives generees au demarrage
Le DataLoader insere automatiquement ~25 transactions reparties sur les 6 dernieres semaines avec des libelles realistes :
- **Loyer mai / avril** (Ahmed -> Fatima, 850 MAD)
- **Salaire freelance / Prime Q1** (Karim -> Ahmed, 1500-2300 MAD)
- **Restaurant, Courses Marjane, Facture EDF, Internet IAM, Netflix, Taxi, Cadeau anniversaire**, etc.
- 3 **depots externes** simulant des salaires (Recharge, Salaire avril, Mission consultant)
- 1 **retrait DAB** (400 MAD)
- 1 transaction **echouee** pour solde insuffisant (visible dans l'ecran Statistiques)
- **Beneficiaires croises** : chaque compte principal a les 2 autres comme beneficiaires enregistres

Ces donnees permettent de tester immediatement le virement, le retrait, l'historique, les beneficiaires et les **statistiques** sans rien creer manuellement.

---

## Tester le backend avec Postman / curl

### Signup
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"fullName\":\"Test User\",\"email\":\"test@test.com\",\"password\":\"123456\",\"phone\":\"0600000000\"}"
```

### Verifier l'OTP
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@test.com\",\"otp\":\"123456\"}"
```

### Login (recupere le token)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test1@bank.com\",\"password\":\"password123\"}"
```

### Endpoints proteges (token requis)
```bash
TOKEN="le_token_recupere_par_login"
curl http://localhost:8080/api/account/info -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/api/transactions/history -H "Authorization: Bearer $TOKEN"
```

---

## Compiler le rapport LaTeX et la presentation

### Rapport
```bash
cd rapport
pdflatex rapport.tex
pdflatex rapport.tex   # 2 fois pour la table des matieres
```
Cela produit `rapport.pdf` (~20 pages).

### Presentation
Le fichier `presentation/presentation.pptx` est deja genere. Pour le regenerer :
```bash
cd presentation
pip install python-pptx
python generate_pptx.py
```

---

## Structure du projet
```
BankingApp/
├── backend/                                  # Spring Boot
│   ├── pom.xml
│   ├── README.md
│   └── src/main/
│       ├── java/com/banking/
│       │   ├── BankingApplication.java
│       │   ├── config/DataLoader.java
│       │   ├── controller/                   # AuthController, AccountController, TransactionController, GlobalExceptionHandler
│       │   ├── dto/                          # SignupRequest, OtpRequest, LoginRequest, etc.
│       │   ├── entity/                       # User, Account, Transaction, OtpToken
│       │   ├── repository/                   # UserRepository, AccountRepository, ...
│       │   ├── security/                     # JwtUtil, JwtAuthFilter, SecurityConfig
│       │   └── service/                      # AuthService, OtpService, EmailService, AccountService, TransactionService
│       └── resources/application.properties
│
├── frontend/                                 # Android Java
│   ├── settings.gradle, build.gradle, gradle.properties
│   ├── README.md
│   └── app/
│       ├── build.gradle
│       └── src/main/
│           ├── AndroidManifest.xml
│           ├── java/com/banking/app/
│           │   ├── activities/               # SplashActivity, SignupActivity, OtpVerificationActivity, LoginActivity, DashboardActivity
│           │   ├── adapters/TransactionAdapter.java
│           │   ├── models/                   # SignupRequest, OtpRequest, LoginRequest, AccountInfo, Transaction, ...
│           │   ├── network/                  # ApiConfig, ApiService, RetrofitClient, AuthInterceptor
│           │   └── utils/                    # SessionManager, ApiErrorParser
│           └── res/
│               ├── drawable/                 # logo, logout, fond carte solde
│               ├── layout/                   # 4 activities + 2 dialogs + item_transaction
│               ├── mipmap-anydpi-v26/        # ic_launcher
│               └── values/                   # colors.xml, strings.xml, themes.xml
│
├── rapport/                                  # LaTeX
│   ├── rapport.tex
│   ├── pagedegarde.tex
│   └── images/
│
├── presentation/                             # PowerPoint
│   ├── generate_pptx.py
│   └── presentation.pptx                     # 15 slides generees
│
├── instructions.txt
└── README.md                                 # ce fichier
```

---

## En cas de souci

| Probleme | Solution |
|----------|----------|
| `connect ECONNREFUSED 10.0.2.2:8080` sur l'app | Le backend n'est pas lance. Demarrer `mvn spring-boot:run`. |
| Le mail OTP n'arrive pas | Verifier que `app.email.simulate=false` et que les credentials Gmail sont valides, ou laisser `simulate=true` et regarder la console. |
| L'app crashe au demarrage | Verifier la version Android Studio + plugin AGP 8.2.2. Faire `File > Invalidate Caches and Restart`. |
| `mvn` introuvable | Installer Maven et l'ajouter au PATH. |
| Conflit MySQL / H2 | Par defaut H2 est utilise ; aucune installation MySQL requise. |
| Emulateur lent | Activer la virtualisation dans le BIOS (Intel VT-x / AMD-V). |
| Mot de passe Gmail refuse | Il faut un **App Password**, pas le mot de passe principal (necessite 2FA activee). |

Bonne demonstration !
