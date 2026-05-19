package com.banking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.banking.app.R;
import com.banking.app.databinding.ActivityLoginBinding;
import com.banking.app.models.LoginRequest;
import com.banking.app.models.LoginResponse;
import com.banking.app.network.RetrofitClient;
import com.banking.app.utils.ApiErrorParser;
import com.banking.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Ecran de connexion : email + mot de passe + JWT.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);
        binding.btnLogin.setOnClickListener(v -> doLogin());
        binding.tvGoSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });
    }

    private void doLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String pwd = binding.etPassword.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast(getString(R.string.error_invalid_email)); return; }
        if (TextUtils.isEmpty(pwd)) { toast(getString(R.string.error_password_required)); return; }

        setLoading(true);
        RetrofitClient.getApiService(this).login(new LoginRequest(email, pwd))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse body = response.body();
                            session.saveSession(body.token, body.email, body.fullName, body.accountNumber);
                            toast(getString(R.string.welcome) + " " + body.fullName);
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            toast(ApiErrorParser.parse(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        setLoading(false);
                        toast(getString(R.string.error_network) + " : " + t.getMessage());
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
}
