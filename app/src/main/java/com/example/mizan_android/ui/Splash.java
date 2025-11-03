package com.example.mizan_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.R;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.User;
import com.example.mizan_android.data.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Splash extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private ImageView imageView;
    private Animation animation;
    private ProgressBar progressBar;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        AppDatabase db = ((MizanApplication) getApplicationContext()).getDatabase();
        final UserDao userDao = db.userDao();

        progressBar = findViewById(R.id.progressBar);
        layout = findViewById(R.id.splashLayout);
        imageView = findViewById(R.id.ivSplashIcon);
        animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

        // Set listener BEFORE starting the animation so we catch onAnimationStart
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Query DB off the UI thread
                executor.execute(() -> {
                    User user = userDao.getLoggedInUser();
                    final boolean isLoggedIn = (user != null);

                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (!isFinishing()) {
                            Intent intent = new Intent(Splash.this, isLoggedIn ? MainActivity.class : LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        // Start animation after 3 seconds
        handler.postDelayed(() -> {
            if (!isFinishing()) {
                imageView.startAnimation(animation);
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}
