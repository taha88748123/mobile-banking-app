package com.banking.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.banking.app.R;
import com.banking.app.databinding.ActivityProfileBinding;
import com.banking.app.databinding.DialogChangePasswordBinding;
import com.banking.app.models.ApiMessage;
import com.banking.app.models.ChangePasswordRequest;
import com.banking.app.models.ProfileResponse;
import com.banking.app.models.UpdateProfileRequest;
import com.banking.app.network.RetrofitClient;
import com.banking.app.utils.ApiErrorParser;
import com.banking.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Ecran de profil : consulter / modifier infos + changer mot de passe.
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> save());
        binding.btnChangePassword.setOnClickListener(v -> openChangePasswordDialog());

        loadProfile();
    }

    private void loadProfile() {
        setLoading(true);
        RetrofitClient.getApiService(this).getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse p = response.body();
                    binding.etFullName.setText(p.fullName);
                    binding.etPhone.setText(p.phone);
                    binding.tvEmail.setText(p.email);
                    binding.tvAccountNumber.setText(p.accountNumber);
                    binding.tvAccountType.setText(p.accountType);
                    binding.tvCreatedAt.setText(p.createdAt != null && p.createdAt.length() >= 10
                            ? p.createdAt.substring(0, 10) : "");
                } else {
                    toast(ApiErrorParser.parse(response));
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                setLoading(false);
                toast(getString(R.string.error_network) + " : " + t.getMessage());
            }
        });
    }

    private void save() {
        String name = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(name)) { toast(getString(R.string.error_name_required)); return; }
        if (TextUtils.isEmpty(phone)) { toast(getString(R.string.error_phone_required)); return; }

        setLoading(true);
        RetrofitClient.getApiService(this).updateProfile(new UpdateProfileRequest(name, phone))
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            session.saveSession(session.getToken(), session.getEmail(),
                                    response.body().fullName, session.getAccountNumber());
                            toast(getString(R.string.profile_updated));
                        } else {
                            toast(ApiErrorParser.parse(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {
                        setLoading(false);
                        toast(getString(R.string.error_network) + " : " + t.getMessage());
                    }
                });
    }

    private void openChangePasswordDialog() {
        DialogChangePasswordBinding b = DialogChangePasswordBinding.inflate(LayoutInflater.from(this));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.change_password)
                .setView(b.getRoot())
                .setPositiveButton(R.string.confirm, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String oldP = b.etCurrent.getText().toString();
            String newP = b.etNew.getText().toString();
            String confirm = b.etConfirm.getText().toString();
            if (TextUtils.isEmpty(oldP) || TextUtils.isEmpty(newP)) {
                toast(getString(R.string.error_fields_required)); return;
            }
            if (newP.length() < 6) { toast(getString(R.string.error_password_short)); return; }
            if (!newP.equals(confirm)) { toast(getString(R.string.error_password_mismatch)); return; }

            RetrofitClient.getApiService(this).changePassword(new ChangePasswordRequest(oldP, newP))
                    .enqueue(new Callback<ApiMessage>() {
                        @Override
                        public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                            if (response.isSuccessful()) {
                                toast(getString(R.string.password_changed));
                                dialog.dismiss();
                            } else {
                                toast(ApiErrorParser.parse(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiMessage> call, Throwable t) {
                            toast(getString(R.string.error_network) + " : " + t.getMessage());
                        }
                    });
        }));
        dialog.show();
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!loading);
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
}
