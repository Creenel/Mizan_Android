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

    private byte[] mediaBytes;

    public CaseEntity() {}

    public CaseEntity(int userId, @NonNull String type, String description,
                      @NonNull String status, @NonNull String date, byte[] mediaBytes) {
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.status = status;
        this.date = date;
        this.mediaBytes = mediaBytes;
    }

    public CaseEntity(int userId, @NonNull String type, String description,
                      @NonNull String status, @NonNull String date) {
        this(userId, type, description, status, date, null);
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public byte[] getMediaBytes() {
        return mediaBytes;
    }

    public void setMediaBytes(byte[] mediaBytes) {
        this.mediaBytes = mediaBytes;
    }

}