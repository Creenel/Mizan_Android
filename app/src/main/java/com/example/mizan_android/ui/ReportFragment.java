package com.example.mizan_android.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mizan_android.R;
import com.google.android.material.button.MaterialButton;

public class ReportFragment extends Fragment {

    private EditText reportTitle;
    private EditText reportDescription;
    private MaterialButton submitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        reportTitle = view.findViewById(R.id.reportTitle);
        reportDescription = view.findViewById(R.id.reportDescription);
        submitButton = view.findViewById(R.id.submitReportButton);

        submitButton.setOnClickListener(v -> handleSubmit());

        return view;
    }

    private void handleSubmit() {
        String title = reportTitle.getText().toString().trim();
        String description = reportDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Save report to Room or send to server if needed
        Toast.makeText(getContext(), "Report submitted successfully!", Toast.LENGTH_LONG).show();

        // Reset fields
        reportTitle.setText("");
        reportDescription.setText("");
    }
}
