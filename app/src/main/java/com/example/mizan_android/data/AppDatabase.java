package com.example.mizan_android.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.mizan_android.data.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
