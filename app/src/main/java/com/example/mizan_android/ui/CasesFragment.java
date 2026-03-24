package com.example.mizan_android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.R;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.CaseEntity;
import com.example.mizan_android.data.User;
import com.example.mizan_android.data.UserDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.BitmapFactory;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;

public class CasesFragment extends Fragment {

    private RecyclerView recyclerCases;
    private LinearLayout emptyState;
    private Spinner spinnerSort;
    private CasesAdapter adapter;
    private CasesViewModel viewModel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private List<CaseEntity> currentCases = null;

    private static final String[] SORT_OPTIONS = new String[]{
            "Date (newest first)",
            "Crime type (A→Z)"
    };

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M/yyyy"); // matches ReportFragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cases, container, false);

        recyclerCases = root.findViewById(R.id.recycler_cases);
        emptyState = root.findViewById(R.id.emptyState);
        spinnerSort = root.findViewById(R.id.spinner_sort);

        adapter = new CasesAdapter(this::showCaseMedia);
        recyclerCases.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCases.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CasesViewModel.class);

        setupSpinner();

        executor.execute(() -> {
            AppDatabase db = ((MizanApplication) requireActivity().getApplicationContext()).getDatabase();
            UserDao userDao = db.userDao();

            User logged = null;
            try {
                logged = userDao.getLoggedInUser();
            } catch (Exception ignored) {}

            if (logged == null) {
                requireActivity().runOnUiThread(() -> {
                    recyclerCases.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                });
                return;
            }

            final int userId = logged.getUserId();
            requireActivity().runOnUiThread(() -> attachLiveData(userId));
        });

        return root;
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, SORT_OPTIONS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);
        spinnerSort.setSelection(0, false);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentCases != null) {
                    applySortAndShow(new ArrayList<>(currentCases));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void attachLiveData(int userId) {
        try {
            viewModel.loadCases(userId);

            LiveData<List<CaseEntity>> ld = viewModel.getCases();
            if (ld == null) {
                doFallbackSyncReadAndShow();
                return;
            }

            ld.observe(getViewLifecycleOwner(), list -> {
                if (list == null || list.isEmpty()) {
                    doFallbackSyncReadAndShow();
                    return;
                }
                currentCases = new ArrayList<>(list);
                applySortAndShow(new ArrayList<>(list));
            });

        } catch (Exception ignored) {
            doFallbackSyncReadAndShow();
        }
    }

    private void doFallbackSyncReadAndShow() {
        executor.execute(() -> {
            try {
                AppDatabase db = ((MizanApplication) requireActivity().getApplicationContext()).getDatabase();
                User logged = db.userDao().getLoggedInUser();
                if (logged == null) {
                    requireActivity().runOnUiThread(() -> {
                        recyclerCases.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    });
                    return;
                }

                List<CaseEntity> syncList = db.caseDao().getCasesForUserDebug(logged.getUserId());

                requireActivity().runOnUiThread(() -> {
                    if (syncList != null && !syncList.isEmpty()) {
                        currentCases = new ArrayList<>(syncList);
                        applySortAndShow(new ArrayList<>(syncList));
                        recyclerCases.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                    } else {
                        recyclerCases.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });

            } catch (Exception ignored) {
                requireActivity().runOnUiThread(() -> {
                    recyclerCases.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void showCaseMedia(CaseEntity caseItem) {
        executor.execute(() -> {
            AppDatabase db = ((MizanApplication) requireActivity().getApplicationContext()).getDatabase();

            byte[] media = db.caseDao().getMediaBytesByCaseId(caseItem.getCaseId());

            requireActivity().runOnUiThread(() -> {
                if (media == null || media.length == 0) {
                    Toast.makeText(requireContext(), "No media", Toast.LENGTH_SHORT).show();
                    return;
                }

                ImageView imageView = new ImageView(requireContext());
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(
                        android.graphics.BitmapFactory.decodeByteArray(media, 0, media.length)
                );

                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Case Media")
                        .setView(imageView)
                        .setPositiveButton("Close", null)
                        .show();
            });
        });
    }

    private void applySortAndShow(List<CaseEntity> list) {
        if (list == null) return;

        int selection = spinnerSort != null ? spinnerSort.getSelectedItemPosition() : 0;

        if (selection == 0) {
            Collections.sort(list, (a, b) -> {
                Date da = parseDateSafe(a.getDate());
                Date db = parseDateSafe(b.getDate());
                return db.compareTo(da);
            });
        } else {
            Collections.sort(list, (a, b) -> a.getType().compareToIgnoreCase(b.getType()));
        }

        currentCases = new ArrayList<>(list);
        adapter.setCases(list);

        boolean empty = list.isEmpty();
        recyclerCases.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private Date parseDateSafe(String s) {
        if (s == null || s.isEmpty()) return new Date(0L);
        try {
            return DATE_FORMAT.parse(s);
        } catch (ParseException e) {
            return new Date(0L);
        }
    }
}