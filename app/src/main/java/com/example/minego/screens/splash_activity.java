package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.utils.SharedPreferencesUtil;

public class splash_activity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DISPLAY_TIME = 3000; // 3 seconds
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (SharedPreferencesUtil.isUserLoggedIn(this)) {
                Log.d(TAG, "User signed in, redirecting to MainActivity");
                intent = new Intent(splash_activity.this, MainActivity.class);
            } else {
                Log.d(TAG, "User not signed in, redirecting to LandingActivity");
                intent = new Intent(splash_activity.this, LandingActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }, SPLASH_DISPLAY_TIME);
    }
}





