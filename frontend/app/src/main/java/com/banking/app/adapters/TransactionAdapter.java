package com.banking.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banking.app.R;
import com.banking.app.models.Transaction;

import java.util.List;

/**
 * Adapter RecyclerView pour afficher les transactions sur le dashboard.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TxHolder> {

    private final List<Transaction> data;
    private final String currentAccount;

    public TransactionAdapter(List<Transaction> data, String currentAccount) {
        this.data = data;
        this.currentAccount = currentAccount;
    }

    @NonNull
    @Override
    public TxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TxHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TxHolder h, int position) {
        Transaction t = data.get(position);
        boolean incoming = currentAccount != null && currentAccount.equals(t.toAccount);
        String prefix = incoming ? "+" : "-";
        int color = incoming ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");

        h.txType.setText(t.type + (incoming ? " (recu)" : " (envoye)"));
        h.txDescription.setText(t.description != null ? t.description : "");
        h.txAmount.setText(prefix + " " + t.amount + " MAD");
        h.txAmount.setTextColor(color);
        h.txDate.setText(t.timestamp != null ? t.timestamp.replace("T", " ").substring(0, Math.min(16, t.timestamp.length())) : "");
        h.txOther.setText(incoming ? "De : " + t.fromAccount : "Vers : " + t.toAccount);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TxHolder extends RecyclerView.ViewHolder {
        TextView txType, txAmount, txDescription, txDate, txOther;

        TxHolder(@NonNull View itemView) {
            super(itemView);
            txType = itemView.findViewById(R.id.txType);
            txAmount = itemView.findViewById(R.id.txAmount);
            txDescription = itemView.findViewById(R.id.txDescription);
            txDate = itemView.findViewById(R.id.txDate);
            txOther = itemView.findViewById(R.id.txOther);
        }
    }
}
