package com.example.mizan_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;

import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.R;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.UserDao;
import com.example.mizan_android.data.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If EdgeToEdge helper isn't available on your project remove/replace it:
        // EdgeToEdge.enable(this);
        // Safe alternative using WindowCompat (core library):
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        AppDatabase db = ((MizanApplication) getApplicationContext()).getDatabase();
        UserDao userDao = db.userDao();

        // Run DB query off the main thread
        executor.execute(() -> {
            try {
                User loggedIn = userDao.getLoggedInUser(); // blocking call
                if (loggedIn == null) {
                    runOnUiThread(() -> {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish(); // prevent returning to this activity
                    });
                }
                // else: user exists, continue normally
            } catch (Exception e) {
                // Log/print exception so you can see it in Logcat
                e.printStackTrace();

                // fallback: if DB query fails (e.g. missing column), send to login
                runOnUiThread(() -> {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                });
            }
        });

        // Apply window insets safely
        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
