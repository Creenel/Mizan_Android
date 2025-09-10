package com.example.mizan_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.R;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.User;
import com.example.mizan_android.data.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDatabase db = ((MizanApplication) getApplicationContext()).getDatabase();
        UserDao userDao = db.userDao();

        executor.execute(() -> {
            try {
                User user = userDao.getLoggedInUser();

                if (user == null) {
                    runOnUiThread(() -> {
                        if (!isFinishing()) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    return;
                }

                String fullName = user.getFullName();
                String firstName;
                if (fullName != null && !fullName.trim().isEmpty()) {
                    firstName = fullName.trim().split("\\s+")[0];
                } else if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                    firstName = user.getUsername();
                } else {
                    firstName = "User";
                }
                final String helloTextValue = "Hello, " + firstName + "!";

                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        TextView helloText = findViewById(R.id.helloText);
                        if (helloText != null) {
                            helloText.setText(helloTextValue);
                        }

                        Button signoutButton = findViewById(R.id.signoutButton);
                        signoutButton.setOnClickListener(v -> {
                            executor.execute(() -> {
                                user.setIsLoggedIn(false);
                                userDao.update(user);
                                runOnUiThread(() -> {
                                    if (!isFinishing()) {
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });
                            });
                        });
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}
