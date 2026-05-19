# Frontend Banking App - Android Java

Application Android native ecrite en Java pour interagir avec le backend Spring Boot.

## Stack
- Android Studio (compileSdk 34, minSdk 24)
- Java 17
- Retrofit2 + OkHttp + Gson
- Material Components 1.12
- ViewBinding

## 4 ecrans principaux
1. `SignupActivity` - inscription avec validation
2. `OtpVerificationActivity` - saisie du code OTP a 6 chiffres
3. `LoginActivity` - connexion (sauvegarde du JWT)
4. `DashboardActivity` - solde, virement, depot, historique

Un `SplashActivity` aiguille vers `LoginActivity` ou `DashboardActivity` selon la session.

## URL backend
Configuree dans `com/banking/app/network/ApiConfig.java` :
- Emulateur AVD : `http://10.0.2.2:8080/api/` (defaut)
- Telephone physique : remplacer par l'IP locale du PC, ex `http://192.168.1.10:8080/api/`

## Build & run
1. Ouvrir le dossier `frontend/` dans Android Studio.
2. Laisser Gradle synchroniser.
3. Demarrer un AVD (API 24+) ou brancher un telephone (mode developpeur + debug USB).
4. Cliquer Run.
