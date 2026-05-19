package com.banking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.banking.app.R;
import com.banking.app.databinding.ActivityOtpBinding;
import com.banking.app.models.ApiMessage;
import com.banking.app.models.OtpRequest;
import com.banking.app.models.ResendOtpRequest;
import com.banking.app.network.RetrofitClient;
import com.banking.app.utils.ApiErrorParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Ecran de saisie du code OTP a 6 chiffres recu par email.
 */
public class OtpVerificationActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private String email;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");
        binding.tvEmailInfo.setText(getString(R.string.otp_sent_to, email != null ? email : ""));

        binding.btnVerify.setOnClickListener(v -> verifyOtp());
        binding.tvResend.setOnClickListener(v -> resendOtp());
        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.cancel();
        binding.tvResend.setEnabled(false);
        timer = new CountDownTimer(60_000, 1000) {
            @Override
            public void onTick(long ms) {
                binding.tvTimer.setText(getString(R.string.otp_expires_in, ms / 1000));
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText(getString(R.string.otp_expired));
                binding.tvResend.setEnabled(true);
            }
        }.start();
    }

    private void verifyOtp() {
        String otp = binding.etOtp.getText().toString().trim();
        if (TextUtils.isEmpty(otp) || otp.length() != 6) {
            toast(getString(R.string.error_otp_length));
            return;
        }
        setLoading(true);
        RetrofitClient.getApiService(this).verifyOtp(new OtpRequest(email, otp))
                .enqueue(new Callback<ApiMessage>() {
                    @Override
                    public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            toast(getString(R.string.account_activated));
                            startActivity(new Intent(OtpVerificationActivity.this, LoginActivity.class));
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

    private void resendOtp() {
        setLoading(true);
        RetrofitClient.getApiService(this).resendOtp(new ResendOtpRequest(email))
                .enqueue(new Callback<ApiMessage>() {
                    @Override
                    public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            toast(getString(R.string.otp_resent));
                            startTimer();
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
        binding.btnVerify.setEnabled(!loading);
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
