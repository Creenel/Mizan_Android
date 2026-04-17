package com.example.mizan_android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mizan_android.R;
import com.example.mizan_android.data.CaseEntity;

import java.util.ArrayList;
import java.util.List;

public class CasesAdapter extends RecyclerView.Adapter<CasesAdapter.CaseViewHolder> {

    //ArrayList is useful for displaying lists that might change in length during runtime
    private List<CaseEntity> cases = new ArrayList<>();

    public interface OnMediaClickListener {
        void onMediaClick(CaseEntity caseEntity);
    }


    private OnMediaClickListener listener;
    //implements OnMediaClickListener, defined at the bottom
    public CasesAdapter(OnMediaClickListener listener) {
        this.listener = listener;
    }

    //for updating cases list
    public void setCases(List<CaseEntity> newCases) {
        cases = newCases;
        notifyDataSetChanged();
    }


    //inflates cases list
    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_case, parent, false);
        return new CaseViewHolder(v);
    }

    //updates RecyclerView list when a new list item comes into the screen
    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        CaseEntity c = cases.get(position);

        holder.type.setText(c.getType());
        holder.date.setText(c.getDate());
        holder.status.setText(c.getStatus());
        holder.description.setText(c.getDescription());

        holder.btnViewMedia.setVisibility(View.VISIBLE);

        holder.btnViewMedia.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMediaClick(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cases.size();
    }


    //Each case card
    static class CaseViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        TextView date;
        TextView status;
        TextView description;
        Button btnViewMedia;

        public CaseViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.txtCaseType);
            date = itemView.findViewById(R.id.txtCaseDate);
            status = itemView.findViewById(R.id.txtCaseStatus);
            description = itemView.findViewById(R.id.txtCaseDescription);
            btnViewMedia = itemView.findViewById(R.id.btnViewMedia);
        }
    }
}