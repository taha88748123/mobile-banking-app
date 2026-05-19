package com.banking.app.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.banking.app.R;
import com.banking.app.databinding.ActivityStatisticsBinding;
import com.banking.app.models.StatisticsResponse;
import com.banking.app.models.Transaction;
import com.banking.app.network.RetrofitClient;
import com.banking.app.utils.ApiErrorParser;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tableau analytique : revenus, depenses, repartition visuelle, top transactions.
 */
public class StatisticsActivity extends AppCompatActivity {

    private ActivityStatisticsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.swipeRefresh.setOnRefreshListener(this::load);
        load();
    }

    private void load() {
        binding.swipeRefresh.setRefreshing(true);
        RetrofitClient.getApiService(this).getStatistics().enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    render(response.body());
                } else {
                    toast(ApiErrorParser.parse(response));
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                toast(getString(R.string.error_network) + " : " + t.getMessage());
            }
        });
    }

    private void render(StatisticsResponse s) {
        binding.tvBalance.setText(money(s.currentBalance));
        binding.tvIncome.setText(money(s.totalIncome));
        binding.tvExpense.setText(money(s.totalExpenses));
        binding.tvNet.setText(money(s.netCashflow));
        binding.tvNet.setTextColor(getResources().getColor(
                s.netCashflow != null && s.netCashflow.signum() >= 0 ? R.color.success : R.color.error, null));

        binding.tvMonthIncome.setText(money(s.monthIncome));
        binding.tvMonthExpenses.setText(money(s.monthExpenses));
        binding.tvMonthDays.setText(getString(R.string.stats_month_days, s.monthDays));

        binding.tvCount.setText(String.valueOf(s.transactionCount));
        binding.tvIncomingCount.setText(String.valueOf(s.incomingCount));
        binding.tvOutgoingCount.setText(String.valueOf(s.outgoingCount));
        binding.tvAverage.setText(money(s.averageTransaction));
        binding.tvLargestIncome.setText(money(s.largestIncome));
        binding.tvLargestExpense.setText(money(s.largestExpense));

        // Barre de repartition revenus / depenses
        int total = (s.totalIncome != null ? s.totalIncome.intValue() : 0)
                + (s.totalExpenses != null ? s.totalExpenses.intValue() : 0);
        if (total > 0) {
            float incomeRatio = s.totalIncome.floatValue() / total;
            binding.barIncome.setLayoutParams(weighted(incomeRatio));
            binding.barExpense.setLayoutParams(weighted(1f - incomeRatio));
            binding.tvIncomePct.setText(String.format(Locale.FRANCE, "%.0f%%", incomeRatio * 100));
            binding.tvExpensePct.setText(String.format(Locale.FRANCE, "%.0f%%", (1f - incomeRatio) * 100));
        } else {
            binding.barIncome.setLayoutParams(weighted(0.5f));
            binding.barExpense.setLayoutParams(weighted(0.5f));
            binding.tvIncomePct.setText("0%");
            binding.tvExpensePct.setText("0%");
        }

        fillTopList(binding.containerTopExpenses, s.topExpenses, false);
        fillTopList(binding.containerTopIncomes, s.topIncomes, true);
    }

    private LinearLayout.LayoutParams weighted(float weight) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, Math.max(weight, 0.01f));
        return lp;
    }

    private void fillTopList(LinearLayout container, List<Transaction> items, boolean incoming) {
        container.removeAllViews();
        if (items == null || items.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(R.string.no_transactions);
            empty.setPadding(0, 16, 0, 16);
            container.addView(empty);
            return;
        }
        BigDecimal max = items.get(0).amount != null ? items.get(0).amount : BigDecimal.ONE;
        if (max.signum() == 0) max = BigDecimal.ONE;
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Transaction t : items) {
            View row = inflater.inflate(R.layout.item_stat_row, container, false);
            TextView title = row.findViewById(R.id.statTitle);
            TextView amount = row.findViewById(R.id.statAmount);
            View bar = row.findViewById(R.id.statBar);
            title.setText(t.description != null && !t.description.isEmpty()
                    ? t.description : (incoming ? "Revenu" : "Depense"));
            String prefix = incoming ? "+ " : "- ";
            amount.setText(prefix + money(t.amount));
            amount.setTextColor(getResources().getColor(
                    incoming ? R.color.success : R.color.error, null));

            float ratio = t.amount.floatValue() / max.floatValue();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bar.getLayoutParams();
            lp.weight = Math.max(ratio, 0.02f);
            bar.setLayoutParams(lp);
            container.addView(row);
        }
    }

    private String money(BigDecimal a) {
        if (a == null) return "0,00 MAD";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(a) + " MAD";
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
}
