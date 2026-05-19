package com.banking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.banking.app.R;
import com.banking.app.databinding.ActivitySignupBinding;
import com.banking.app.models.ApiMessage;
import com.banking.app.models.SignupRequest;
import com.banking.app.network.RetrofitClient;
import com.banking.app.utils.ApiErrorParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Ecran d'inscription : nom, email, telephone, mot de passe.
 */
public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignup.setOnClickListener(v -> doSignup());
        binding.tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void doSignup() {
        String name = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String pwd = binding.etPassword.getText().toString();
        String pwd2 = binding.etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name)) { toast(getString(R.string.error_name_required)); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast(getString(R.string.error_invalid_email)); return; }
        if (TextUtils.isEmpty(phone)) { toast(getString(R.string.error_phone_required)); return; }
        if (pwd.length() < 6) { toast(getString(R.string.error_password_short)); return; }
        if (!pwd.equals(pwd2)) { toast(getString(R.string.error_password_mismatch)); return; }

        setLoading(true);
        SignupRequest req = new SignupRequest(name, email, pwd, phone);
        RetrofitClient.getApiService(this).signup(req).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    toast(getString(R.string.signup_success));
                    Intent i = new Intent(SignupActivity.this, OtpVerificationActivity.class);
                    i.putExtra("email", email);
                    startActivity(i);
                    finish();
                } else {
                    toast(ApiErrorParser.parse(response));
                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                setLoading(false);
                toast(getString(R.string.error_network) + " : " + t.getMessage());
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSignup.setEnabled(!loading);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
