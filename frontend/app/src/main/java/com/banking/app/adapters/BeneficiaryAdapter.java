package com.banking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banking.app.R;
import com.banking.app.models.Beneficiary;

import java.util.List;

/**
 * Adapter RecyclerView pour la liste des beneficiaires.
 */
public class BeneficiaryAdapter extends RecyclerView.Adapter<BeneficiaryAdapter.Holder> {

    public interface Listener {
        void onTransfer(Beneficiary b);
        void onDelete(Beneficiary b);
    }

    private final List<Beneficiary> data;
    private final Listener listener;

    public BeneficiaryAdapter(List<Beneficiary> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_beneficiary, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Beneficiary b = data.get(position);
        h.label.setText(b.label);
        h.accountNumber.setText(b.accountNumber);
        h.beneficiaryName.setText(b.beneficiaryName != null ? b.beneficiaryName : "");
        h.btnSend.setOnClickListener(v -> listener.onTransfer(b));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(b));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView label, accountNumber, beneficiaryName;
        ImageButton btnSend, btnDelete;

        Holder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.bnLabel);
            accountNumber = itemView.findViewById(R.id.bnAccount);
            beneficiaryName = itemView.findViewById(R.id.bnName);
            btnSend = itemView.findViewById(R.id.bnSend);
            btnDelete = itemView.findViewById(R.id.bnDelete);
        }
    }
}
