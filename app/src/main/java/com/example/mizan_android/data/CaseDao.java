package com.example.mizan_android.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CaseDao {

    @Insert
    void insert(CaseEntity caseEntity);

    @Query("SELECT * FROM case_table WHERE userId = :userId")
    List<CaseEntity> getCasesForUser(int userId);

    //media is fetched with getMediaBytesByCaseId
    @Query(" SELECT caseId, userId, type, description, date, status, NULL as mediaBytes FROM case_table WHERE userId = :userId")
    LiveData<List<CaseEntity>> getCasesWithoutMedia(int userId);

    @Query("SELECT mediaBytes FROM case_table WHERE caseId = :caseId LIMIT 1")
    byte[] getMediaBytesByCaseId(int caseId);
}