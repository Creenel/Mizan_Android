package com.example.mizan_android.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;

    private String fullName;

    @NonNull
    private String passwordStorage;

    private boolean isLoggedIn;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordStorage() {
        return passwordStorage;
    }

    public void setPasswordStorage(String passwordStorage) {
        this.passwordStorage = passwordStorage;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public User(@NonNull String username, @NonNull String passwordStorage, String fullName) {
        this.fullName = fullName;
        this.username = username;
        this.passwordStorage = passwordStorage;
    }


}
