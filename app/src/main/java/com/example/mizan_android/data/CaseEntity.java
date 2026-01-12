package com.example.mizan_android.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "case_table",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class CaseEntity {

    @PrimaryKey(autoGenerate = true)
    private int caseId;

    private int userId;

    @NonNull
    private String type;
    private String description;
    @NonNull
    private String date;
    @NonNull
    private String status;


    public CaseEntity(int userId,
                      @NonNull String type,
                      String description,
                      @NonNull String status,
                      String date) {
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    // getters / setters
}

