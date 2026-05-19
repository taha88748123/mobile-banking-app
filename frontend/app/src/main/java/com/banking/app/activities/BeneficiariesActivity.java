package com.banking.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.banking.app.R;
import com.banking.app.adapters.BeneficiaryAdapter;
import com.banking.app.databinding.ActivityBeneficiariesBinding;
import com.banking.app.databinding.DialogAddBeneficiaryBinding;
import com.banking.app.databinding.DialogTransferBinding;
import com.banking.app.models.AddBeneficiaryRequest;
import com.banking.app.models.ApiMessage;
import com.banking.app.models.Beneficiary;
import com.banking.app.models.Transaction;
import com.banking.app.models.TransferRequest;
import com.banking.app.network.RetrofitClient;
import com.banking.app.utils.ApiErrorParser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Ecran de gestion des beneficiaires : liste, ajout, suppression, virement rapide.
 */
public class BeneficiariesActivity extends AppCompatActivity implements BeneficiaryAdapter.Listener {

    private ActivityBeneficiariesBinding binding;
    private final List<Beneficiary> data = new ArrayList<>();
    private BeneficiaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBeneficiariesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.rvBeneficiaries.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BeneficiaryAdapter(data, this);
        binding.rvBeneficiaries.setAdapter(adapter);
        binding.fabAdd.setOnClickListener(v -> openAddDialog());
        binding.swipeRefresh.setOnRefreshListener(this::load);

        load();
    }

    private void load() {
        binding.swipeRefresh.setRefreshing(true);
        RetrofitClient.getApiService(this).listBeneficiaries().enqueue(new Callback<List<Beneficiary>>() {
            @Override
            public void onResponse(Call<List<Beneficiary>> call, Response<List<Beneficiary>> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    data.clear();
                    data.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    binding.tvEmpty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    toast(ApiErrorParser.parse(response));
                }
            }

            @Override
            public void onFailure(Call<List<Beneficiary>> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                toast(getString(R.string.error_network) + " : " + t.getMessage());
            }
        });
    }

    private void openAddDialog() {
        DialogAddBeneficiaryBinding b = DialogAddBeneficiaryBinding.inflate(LayoutInflater.from(this));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_beneficiary)
                .setView(b.getRoot())
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String label = b.etLabel.getText().toString().trim();
            String account = b.etAccount.getText().toString().trim();
            if (TextUtils.isEmpty(label) || TextUtils.isEmpty(account)) {
                toast(getString(R.string.error_fields_required)); return;
            }
            RetrofitClient.getApiService(this).addBeneficiary(new AddBeneficiaryRequest(label, account))
                    .enqueue(new Callback<Beneficiary>() {
                        @Override
                        public void onResponse(Call<Beneficiary> call, Response<Beneficiary> response) {
                            if (response.isSuccessful()) {
                                toast(getString(R.string.beneficiary_added));
                                dialog.dismiss();
                                load();
                            } else {
                                toast(ApiErrorParser.parse(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<Beneficiary> call, Throwable t) {
                            toast(getString(R.string.error_network) + " : " + t.getMessage());
                        }
                    });
        }));
        dialog.show();
    }

    @Override
    public void onTransfer(Beneficiary b) {
        DialogTransferBinding db = DialogTransferBinding.inflate(LayoutInflater.from(this));
        db.etToAccount.setText(b.accountNumber);
        db.etToAccount.setEnabled(false);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.transfer_to_label, b.label))
                .setView(db.getRoot())
                .setPositiveButton(R.string.send, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String amountStr = db.etAmount.getText().toString().trim();
            String desc = db.etDescription.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) { toast(getString(R.string.error_fields_required)); return; }
            BigDecimal amount;
            try { amount = new BigDecimal(amountStr); }
            catch (NumberFormatException e) { toast(getString(R.string.error_invalid_amount)); return; }

            RetrofitClient.getApiService(this)
                    .transfer(new TransferRequest(b.accountNumber, amount, desc))
                    .enqueue(new Callback<Transaction>() {
                        @Override
                        public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                            if (response.isSuccessful()) {
                                toast(getString(R.string.transfer_success));
                                dialog.dismiss();
                            } else {
                                toast(ApiErrorParser.parse(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<Transaction> call, Throwable t) {
                            toast(getString(R.string.error_network) + " : " + t.getMessage());
                        }
                    });
        }));
        dialog.show();
    }

    @Override
    public void onDelete(Beneficiary b) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm)
                .setMessage(getString(R.string.delete_beneficiary_confirm, b.label))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    RetrofitClient.getApiService(this).deleteBeneficiary(b.id)
                            .enqueue(new Callback<ApiMessage>() {
                                @Override
                                public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                                    if (response.isSuccessful()) {
                                        toast(getString(R.string.beneficiary_deleted));
                                        load();
                                    } else {
                                        toast(ApiErrorParser.parse(response));
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiMessage> call, Throwable t) {
                                    toast(getString(R.string.error_network) + " : " + t.getMessage());
                                }
                            });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
}
