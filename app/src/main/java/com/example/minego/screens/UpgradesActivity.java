package com.example.minego.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Upgrade;
import com.example.minego.models.User;
import com.example.minego.utils.SharedPreferencesUtil;

public class UpgradesActivity extends AppCompatActivity {

    Button btn_upgrade_1, btn_upgrade_2, btn_upgrade_3, btn_upgrade_4;
    ProgressBar pb_upgrade_1, pb_upgrade_2, pb_upgrade_3, pb_upgrade_4;
    ImageView iv_upgrade_1_image;
    int MaxUpgradeMineLevel, MaxUpgradeRadius, MaxUpgradeEfficiency;
    Upgrade upgrade;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upgrades);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user = SharedPreferencesUtil.getUser(this);
        btn_upgrade_1 = findViewById(R.id.btn_upgrade_1);
        btn_upgrade_2 = findViewById(R.id.btn_upgrade_2);
        btn_upgrade_3 = findViewById(R.id.btn_upgrade_3);
        btn_upgrade_4 = findViewById(R.id.btn_upgrade_4);
        pb_upgrade_1 = findViewById(R.id.pb_upgrade_1);
        pb_upgrade_2 = findViewById(R.id.pb_upgrade_2);
        pb_upgrade_3 = findViewById(R.id.pb_upgrade_3);
        pb_upgrade_4 = findViewById(R.id.pb_upgrade_4);
        iv_upgrade_1_image = findViewById(R.id.iv_upgrade_1_image);
        upgrade = user.getUpgrade();

        pb_upgrade_1.setMax(100);
        pb_upgrade_2.setMax(100);
        pb_upgrade_3.setMax(100);
        pb_upgrade_4.setMax(100);

        updateProgressBars();

        btn_upgrade_1.setOnClickListener(v -> {
            if (upgrade.UpgradeMineLevel(this)) {
                updateProgressBars();
            }
        });
        btn_upgrade_2.setOnClickListener(v -> {
            if (upgrade.UpgradeRadius(this)) {
                updateProgressBars();
            }
        });
        btn_upgrade_3.setOnClickListener(v -> {
            if (upgrade.Upgradeefficiency(this)) {
                updateProgressBars();
            }
        });


    }

    private void updateProgressBars() {
        // זה בודק מה הרמה של המכרה מכפיל אותו ב 100
        // ואז מחלק את זה בכמות הרמות שיש לאותו שיפור
        MaxUpgradeMineLevel = (int) Math.round((upgrade.getMineLevel() * 100.0) / Math.max(1, upgrade.MaxUpgradeMineLevel()));
        // מעדכן את המד התקדמות לפי האחוז שיצא מקודם
        pb_upgrade_1.setProgress(MaxUpgradeMineLevel);
        // מעדכן את הרמה שלך
        iv_upgrade_1_image.setImageResource(upgrade.imageminelevel());

        MaxUpgradeRadius = (int) Math.round((upgrade.getRadius_Level() * 100.0) / Math.max(1, upgrade.MaxUpgradeRadius()));
        pb_upgrade_2.setProgress(MaxUpgradeRadius);

        MaxUpgradeEfficiency = (int) Math.round((upgrade.getEfficiency() * 100.0) / Math.max(1, upgrade.MaxUpgradeEfficiency()));
        pb_upgrade_3.setProgress(MaxUpgradeEfficiency);
    }
}