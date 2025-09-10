package com.example.mizan_android.ui;

import static com.example.mizan_android.MizanApplication.getDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.User;
import com.example.mizan_android.data.UserDao;
import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.utils.PasswordUtils;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mizan_android.R;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        AppDatabase db = ((MizanApplication) getApplicationContext()).getDatabase();
        UserDao userDao = db.userDao();

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText nameInput = findViewById(R.id.nameInput);

        Button signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String fullName = nameInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    if (userDao.countUsersWithUsername(email) > 0) {
                        runOnUiThread(() -> Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    String passwordStorageValue = PasswordUtils.generatePasswordStorage(password);

                    User newUser = new User(email, passwordStorageValue, fullName);
                    userDao.insert(newUser);

                    // Fetch and update while still on background thread
                    User user = userDao.getUserByUsername(email);
                    if (user != null) {
                        user.setIsLoggedIn(true);
                        userDao.update(user);
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(SignupActivity.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        TextView loginTextView = findViewById(R.id.loginText);
        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}