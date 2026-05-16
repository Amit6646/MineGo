package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.User;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;

public class splash_activity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_TIME_MS = 3000;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean navigationDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final long splashStartMs = SystemClock.elapsedRealtime();

        Runnable goNext = () -> {
            if (navigationDone || isFinishing()) {
                return;
            }
            navigationDone = true;
            Intent intent;
            if (SharedPreferencesUtil.isUserLoggedIn(splash_activity.this)) {
                intent = new Intent(splash_activity.this, MainActivity.class);
            } else {
                intent = new Intent(splash_activity.this, LandingActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        };

        if (!SharedPreferencesUtil.isUserLoggedIn(this)) {
            mainHandler.postDelayed(goNext, SPLASH_DISPLAY_TIME_MS);
            return;
        }

        User cached = SharedPreferencesUtil.getUser(this);
        if (cached == null || cached.getId() == null || cached.getId().isEmpty()) {
            mainHandler.postDelayed(goNext, SPLASH_DISPLAY_TIME_MS);
            return;
        }

        String uid = cached.getId();

        DatabaseService.getInstance().getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User fromServer) {
                if (fromServer != null) {
                    SharedPreferencesUtil.saveUser(splash_activity.this, fromServer);
                }
                scheduleNavigateAfterSplash(splashStartMs, goNext);
            }

            @Override
            public void onFailed(Exception e) {
                scheduleNavigateAfterSplash(splashStartMs, goNext);
            }
        });
    }

    /**
     * ממתין עד שעבר זמן הספלאש המינימלי, ואז מנווט (כדי שלא “יקפוץ” מסך מוקדם מדי).
     */
    private void scheduleNavigateAfterSplash(long splashStartMs, Runnable goNext) {
        long elapsed = SystemClock.elapsedRealtime() - splashStartMs;
        long remaining = Math.max(0, SPLASH_DISPLAY_TIME_MS - elapsed);
        mainHandler.postDelayed(goNext, remaining);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacksAndMessages(null);
    }
}
