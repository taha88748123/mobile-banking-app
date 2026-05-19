package com.banking.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gestion du token JWT et des infos utilisateur dans SharedPreferences.
 */
public class SessionManager {

    private static final String PREF_NAME = "banking_session";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_ACCOUNT = "account_number";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String email, String name, String accountNumber) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_EMAIL, email)
                .putString(KEY_NAME, name)
                .putString(KEY_ACCOUNT, accountNumber)
                .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getAccountNumber() {
        return prefs.getString(KEY_ACCOUNT, "");
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
