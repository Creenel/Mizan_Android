package com.example.mizan_android.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CasesFragment extends Fragment {

    private RecyclerView recyclerCases;
    private LinearLayout emptyState;
    private Spinner spinnerSort;
    private CasesAdapter adapter;
    private CasesViewModel viewModel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cases, container, false);

        recyclerCases = root.findViewById(R.id.recyclerCases);
        emptyState = root.findViewById(R.id.emptyState);

        adapter = new CasesAdapter();
        recyclerCases.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCases.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CasesViewModel.class);

        // load user + attach observers
        executor.execute(() -> {
            AppDatabase db = ((MizanApplication) requireActivity().getApplicationContext()).getDatabase();
            UserDao userDao = db.userDao();

            Log.d("DB_DEBUG", "CasesFragment: AppDatabase instance=" + System.identityHashCode(db));
            User logged = null;
            try {
                logged = userDao.getLoggedInUser();
            } catch (Exception e) {
                Log.e("DB_DEBUG", "getLoggedInUser failed", e);
            }

            if (logged == null) {
                Log.d("DB_DEBUG", "CasesFragment: No logged-in user");
                requireActivity().runOnUiThread(() -> {
                    recyclerCases.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                });
                return;
            }

            final int userId = logged.getUserId();
            Log.d("DB_DEBUG", "CasesFragment: logged userId=" + userId + " username=" + logged.getUsername());

            // run UI operations on main thread
            requireActivity().runOnUiThread(() -> attachLiveData(userId));
        });

        return root;
    }

    // Attach LiveData observer with defensive checks and fallback
    private void attachLiveData(int userId) {
        try {
            Log.d("DB_DEBUG", "Calling viewModel.loadCases(userId=" + userId + ")");
            viewModel.loadCases(userId);

            LiveData<List<CaseEntity>> ld = viewModel.getCases();

            if (ld == null) {
                Log.w("DB_DEBUG", "viewModel.getCases() returned null — doing fallback sync read");
                doFallbackSyncReadAndShow();
                return;
            }

            // Safe lifecycle observer
            ld.observe(getViewLifecycleOwner(), list -> {
                try {
                    int received = (list == null) ? 0 : list.size();
                    Log.d("DB_DEBUG", "LiveData observer -> received=" + received);

                    if (list == null || list.isEmpty()) {
                        // fallback to sync read so UI isn't empty while debugging
                        doFallbackSyncReadAndShow();
                        return;
                    }

                    // normal path: update UI
                    emptyState.setVisibility(View.GONE);
                    recyclerCases.setVisibility(View.VISIBLE);

                    adapter.setCases(list);

                } catch (Exception innerEx) {
                    // log and fallback
                    Log.e("DB_DEBUG", "Exception inside LiveData observer", innerEx);
                    doFallbackSyncReadAndShow();
                }
            });

        } catch (Exception e) {
            // any exception while attaching observer -> fallback
            Log.e("DB_DEBUG", "Failed to attach LiveData observer", e);
            doFallbackSyncReadAndShow();
        }
    }

    // synchronous read fallback (runs async, updates UI on main thread)
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
                Log.d("DB_DEBUG", "Fallback sync read size=" + (syncList == null ? 0 : syncList.size()));

                requireActivity().runOnUiThread(() -> {
                    if (syncList != null && !syncList.isEmpty()) {
                        adapter.setCases(syncList);
                        recyclerCases.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                        Log.d("DB_DEBUG", "Adapter set from fallback sync read, count=" + adapter.getItemCount());
                    } else {
                        recyclerCases.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    }
                });

            } catch (Exception e) {
                Log.e("DB_DEBUG", "Fallback sync read failed", e);
                requireActivity().runOnUiThread(() -> {
                    recyclerCases.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                });
            }
        });
    }
}