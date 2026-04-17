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
                .fallbackToDestructiveMigration() //wipes all data if database is updated without defined migration
                .build();
    }

    public static AppDatabase getDatabase() {
        return appDatabase;
    }
}
