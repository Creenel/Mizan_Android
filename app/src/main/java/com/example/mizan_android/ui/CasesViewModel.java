package com.example.mizan_android.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.CaseDao;
import com.example.mizan_android.data.CaseEntity;

import java.util.List;

public class CasesViewModel extends AndroidViewModel {

    private final CaseDao caseDao;
    private LiveData<List<CaseEntity>> casesLive;

    public CasesViewModel(@NonNull Application application) {
        super(application);
        // Use the application-level singleton to ensure same DB instance everywhere
        AppDatabase db = ((MizanApplication) application).getDatabase();
        caseDao = db.caseDao();
    }

    public void loadCases(int userId) {
        android.util.Log.d("DB_DEBUG", "CasesViewModel.loadCases called with userId=" + userId
                + " viewModelHash=" + System.identityHashCode(this));
        casesLive = caseDao.getCasesForUser(userId);
    }

    public LiveData<List<CaseEntity>> getCases() {
        return casesLive;
    }
}