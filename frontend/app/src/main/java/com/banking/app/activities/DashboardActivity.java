package com.banking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.banking.app.R;
import com.banking.app.adapters.TransactionAdapter;
import com.banking.app.databinding.ActivityDashboardBinding;
import com.banking.app.databinding.DialogDepositBinding;
import com.banking.app.databinding.DialogTransferBinding;
import com.banking.app.models.AccountInfo;
import com.banking.app.models.DepositRequest;
import com.banking.app.models.Transaction;
import com.banking.app.models.TransferRequest;
import com.banking.app.network.RetrofitClient;
import com.banking.app.utils.ApiErrorParser;
import com.banking.app.utils.SessionManager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tableau de bord principal : solde, actions rapides, historique.
 */
public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private SessionManager session;
    private TransactionAdapter adapter;
    private final List<Transaction> transactions = new ArrayList<>();
    private String accountNumber;

    @Override
    protected void onResume() {
        super.onResume();
        if (session != null && session.isLoggedIn()) {
            refreshAll();
            binding.tvGreeting.setText(getString(R.string.hello_user, session.getName()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);
        binding.tvGreeting.setText(getString(R.string.hello_user, session.getName()));

        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(transactions, session.getAccountNumber());
        binding.rvTransactions.setAdapter(adapter);

        binding.btnTransfer.setOnClickListener(v -> openTransferDialog());
        binding.btnDeposit.setOnClickListener(v -> openDepositDialog());
        binding.btnHistory.setOnClickListener(v -> loadHistory());
        binding.btnBeneficiaries.setOnClickListener(v ->
                startActivity(new Intent(this, BeneficiariesActivity.class)));
        binding.btnStatistics.setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));
        binding.btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
        binding.btnLogout.setOnClickListener(v -> {
            session.clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        binding.swipeRefresh.setOnRefreshListener(this::refreshAll);

        refreshAll();
    }

    private void refreshAll() {
        loadAccountInfo();
        loadHistory();
    }

    private void loadAccountInfo() {
        RetrofitClient.getApiService(this).getAccountInfo().enqueue(new Callback<AccountInfo>() {
            @Override
            public void onResponse(Call<AccountInfo> call, Response<AccountInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountInfo info = response.body();
                    accountNumber = info.accountNumber;
                    binding.tvAccountNumber.setText(getString(R.string.account_label, info.accountNumber));
                    binding.tvBalance.setText(formatMoney(info.balance));
                    binding.tvAccountType.setText(info.accountType);
                } else {
                    toast(ApiErrorParser.parse(response));
                }
            }

            @Override
            public void onFailure(Call<AccountInfo> call, Throwable t) {
                toast(getString(R.string.error_network) + " : " + t.getMessage());
            }
        });
    }

    private void loadHistory() {
        binding.swipeRefresh.setRefreshing(true);
        RetrofitClient.getApiService(this).getHistory().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    transactions.clear();
                    int max = Math.min(response.body().size(), 20);
                    transactions.addAll(response.body().subList(0, max));
                    adapter.notifyDataSetChanged();
                    binding.tvEmpty.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    toast(ApiErrorParser.parse(response));
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                toast(getString(R.string.error_network) + " : " + t.getMessage());
            }
        });
    }

    private void openTransferDialog() {
        DialogTransferBinding b = DialogTransferBinding.inflate(LayoutInflater.from(this));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.transfer_title)
                .setView(b.getRoot())
                .setPositiveButton(R.string.send, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String to = b.etToAccount.getText().toString().trim();
            String amountStr = b.etAmount.getText().toString().trim();
            String desc = b.etDescription.getText().toString().trim();
            if (to.isEmpty() || amountStr.isEmpty()) { toast(getString(R.string.error_fields_required)); return; }
            BigDecimal amount;
            try { amount = new BigDecimal(amountStr); }
            catch (NumberFormatException e) { toast(getString(R.string.error_invalid_amount)); return; }

            RetrofitClient.getApiService(this).transfer(new TransferRequest(to, amount, desc))
                    .enqueue(new Callback<Transaction>() {
                        @Override
                        public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                            if (response.isSuccessful()) {
                                toast(getString(R.string.transfer_success));
                                dialog.dismiss();
                                refreshAll();
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

    private void openDepositDialog() {
        DialogDepositBinding b = DialogDepositBinding.inflate(LayoutInflater.from(this));
        b.etAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.deposit_title)
                .setView(b.getRoot())
                .setPositiveButton(R.string.deposit_action, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String amountStr = b.etAmount.getText().toString().trim();
            String desc = b.etDescription.getText().toString().trim();
            if (amountStr.isEmpty()) { toast(getString(R.string.error_fields_required)); return; }
            BigDecimal amount;
            try { amount = new BigDecimal(amountStr); }
            catch (NumberFormatException e) { toast(getString(R.string.error_invalid_amount)); return; }

            RetrofitClient.getApiService(this).deposit(new DepositRequest(amount, desc))
                    .enqueue(new Callback<Transaction>() {
                        @Override
                        public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                            if (response.isSuccessful()) {
                                toast(getString(R.string.deposit_success));
                                dialog.dismiss();
                                refreshAll();
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

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0.00 MAD";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(amount) + " MAD";
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
}
