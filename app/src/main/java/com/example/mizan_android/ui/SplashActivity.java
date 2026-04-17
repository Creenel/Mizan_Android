package com.example.mizan_android.ui;

import android.content.Context;
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
import android.content.IntentFilter;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.R;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.User;
import com.example.mizan_android.data.UserDao;
import android.os.BatteryManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private ImageView imageView;
    private Animation animation;
    private ProgressBar progressBar;
    private LinearLayout layout;

    private TextView mBatteryLevelText;
    private ProgressBar mBatteryLevelProgress;
    private BroadcastReceiver mReceiver;

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

        mBatteryLevelText = findViewById(R.id.textView);
        mBatteryLevelProgress = findViewById(R.id.progressBar);

        mReceiver = new BatteryBroadcastReceiver();

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                executor.execute(() -> { //manages background threads for tasks
                    try {
                        User user = userDao.getLoggedInUser();
                        final boolean isLoggedIn = (user != null);

                        runOnUiThread(() -> { //goes to login or mainactivity, depending on whether or not user found, at the end of the animation
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            if (!isFinishing()) {
                                Intent intent = new Intent(
                                        SplashActivity.this,
                                        isLoggedIn ? MainActivity.class : LoginActivity.class
                                );
                                startActivity(intent);
                                finish();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            if (!isFinishing()) {
                                Toast.makeText(
                                        SplashActivity.this,
                                        "Database error: " + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) { } //necessary, implementing interface
        });

        handler.postDelayed(() -> {
            if (!isFinishing() && imageView != null) {
                imageView.startAnimation(animation);
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (mReceiver != null)
                registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //shuts down battery receiver
    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mReceiver != null)
                unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    //shuts down executor
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); //clears handler queue, so tasks don't run after activity has been destroyed
        if (!executor.isShutdown()) executor.shutdownNow();
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            if (mBatteryLevelText != null)
                mBatteryLevelText.setText(getString(R.string.battery_level) + "  " + level);
            if (mBatteryLevelProgress != null)
                mBatteryLevelProgress.setProgress(level);
        }
    }
}
