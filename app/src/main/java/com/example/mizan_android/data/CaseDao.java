package com.example.mizan_android.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CaseDao {

    @Insert
    void insert(CaseEntity c);

    @Query(" SELECT * FROM case_table WHERE userId = :userId")
    LiveData<List<CaseEntity>> getCasesForUser(int userId);
}

