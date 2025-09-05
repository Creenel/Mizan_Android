package com.example.mizan_android;

import android.app.Application;
import androidx.room.Room;
import com.example.mizan_android.data.AppDatabase;

public class MizanApplication extends Application {

    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "app_database")
                .build();
    }

    public static AppDatabase getDatabase() {
        return appDatabase;
    }
}
