package com.banking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.banking.app.R;
import com.banking.app.utils.SessionManager;

/**
 * Ecran de demarrage qui oriente vers Login ou Dashboard selon la session.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            Intent intent = new Intent(this,
                    session.isLoggedIn() ? DashboardActivity.class : LoginActivity.class);
            startActivity(intent);
            finish();
        }, 1200);
    }
}
