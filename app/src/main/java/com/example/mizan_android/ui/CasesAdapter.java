package com.example.mizan_android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mizan_android.R;
import com.example.mizan_android.data.CaseEntity;

import java.util.ArrayList;
import java.util.List;

public class CasesAdapter extends RecyclerView.Adapter<CasesAdapter.CaseViewHolder> {

    private List<CaseEntity> cases = new ArrayList<>();

    public void setCases(List<CaseEntity> newCases) {
        cases = newCases;
        notifyDataSetChanged(); // critical
    }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_case, parent, false);
        return new CaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        CaseEntity c = cases.get(position);

        holder.type.setText(c.getType());
        holder.date.setText(c.getDate());
        holder.status.setText(c.getStatus());
        holder.description.setText(c.getDescription());
    }

    @Override
    public int getItemCount() {
        return cases.size();
    }

    static class CaseViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        TextView date;
        TextView status;
        TextView description;

        public CaseViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.txtCaseType);
            date = itemView.findViewById(R.id.txtCaseDate);
            status = itemView.findViewById(R.id.txtCaseStatus);
            description = itemView.findViewById(R.id.txtCaseDescription);
        }
    }
}