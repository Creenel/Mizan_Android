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

        mBatteryLevelText = (TextView) findViewById(R.id.textView);
        mBatteryLevelProgress = (ProgressBar) findViewById(R.id.progressBar);

        mReceiver = new BatteryBroadcastReceiver();
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
                            Intent intent = new Intent(SplashActivity.this, isLoggedIn ? MainActivity.class : LoginActivity.class);
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
    protected void onStart() {
        registerReceiver(mReceiver, new IntentFilter ( Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, 0);

            mBatteryLevelText.setText(getString(R.string.battery_level) + "  " + level);
            mBatteryLevelProgress.setProgress(level);
        }


    }
}
