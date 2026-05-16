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
import com.google.android.material.textfield.TextInputEditText;

public class UpgradesActivity extends AppCompatActivity {

    Button btn_upgrade_1, btn_upgrade_2, btn_upgrade_3, btn_upgrade_4;
    ProgressBar pb_upgrade_1, pb_upgrade_2, pb_upgrade_3, pb_upgrade_4;
    ImageView iv_upgrade_1_image, iv_upgrade_2_image, iv_upgrade_3_image, iv_upgrade_4_image;
    TextInputEditText et_upgrade_1_cost, et_upgrade_2_cost, et_upgrade_3_cost, et_upgrade_4_cost;

    int MaxUpgradeMineLevel, MaxUpgradeRadius, MaxUpgradeEfficiency, MaxUpgradeBackpack;
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
        iv_upgrade_2_image = findViewById(R.id.iv_upgrade_2_image);
        iv_upgrade_3_image = findViewById(R.id.iv_upgrade_3_image);
        iv_upgrade_4_image = findViewById(R.id.iv_upgrade_4_image);
        et_upgrade_1_cost = findViewById(R.id.et_upgrade_1_cost);
        et_upgrade_2_cost = findViewById(R.id.et_upgrade_2_cost);
        et_upgrade_3_cost = findViewById(R.id.et_upgrade_3_cost);
        et_upgrade_4_cost = findViewById(R.id.et_upgrade_4_cost);

        upgrade = user.getUpgrade();

        pb_upgrade_1.setMax(100);
        pb_upgrade_2.setMax(100);
        pb_upgrade_3.setMax(100);
        pb_upgrade_4.setMax(100);

        updateProgressBars();

        btn_upgrade_1.setOnClickListener(v -> {
            if (upgrade.UpgradeMineLevel(UpgradesActivity.this)) {
                updateProgressBars();
            }
        });
        btn_upgrade_2.setOnClickListener(v -> {
            if (upgrade.UpgradeRadius(UpgradesActivity.this)) {
                updateProgressBars();
            }
        });
        btn_upgrade_3.setOnClickListener(v -> {
            if (upgrade.Upgradeefficiency(this)) {
                updateProgressBars();
            }
        });
        btn_upgrade_4.setOnClickListener(v -> {
            if (upgrade.UpgradeBackPack(UpgradesActivity.this)) {
                updateProgressBars();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // טוען מחדש מההעדפות בכל כניסה למסך — אחרת חוזרים לאקטיביטי שכבר בזיכרון ורואים נתונים ישנים
        user = SharedPreferencesUtil.getUser(this);
        if (user == null) {
            finish();
            return;
        }
        upgrade = user.getUpgrade();
        if (upgrade == null) {
            upgrade = new Upgrade(0, 0, 0, 0);
            user.setUpgrade(upgrade);
            SharedPreferencesUtil.saveUser(this, user);
        }
        updateProgressBars();
    }

    private void updateProgressBars() {
        MaxUpgradeMineLevel = (int) Math.round((upgrade.getMineLevel() * 100.0) / Math.max(1, upgrade.MaxUpgradeMineLevel()));
        pb_upgrade_1.setProgress(MaxUpgradeMineLevel);
        iv_upgrade_1_image.setImageResource(upgrade.getMineLevelImage());
        et_upgrade_1_cost.setText(upgrade.getMineUpgradeCostText());


        MaxUpgradeRadius = (int) Math.round((upgrade.getRadiusLevel() * 100.0) / Math.max(1, upgrade.MaxUpgradeRadius()));
        pb_upgrade_2.setProgress(MaxUpgradeRadius);
        iv_upgrade_2_image.setImageResource(upgrade.getRadiusImage());
        et_upgrade_2_cost.setText(upgrade.getRadiusUpgradeCostText());


        MaxUpgradeEfficiency = (int) Math.round((upgrade.getEfficiency() * 100.0) / Math.max(1, upgrade.MaxUpgradeEfficiency()));
        pb_upgrade_3.setProgress(MaxUpgradeEfficiency);
        iv_upgrade_3_image.setImageResource(upgrade.getefficiencyImages());
        et_upgrade_3_cost.setText(upgrade.getEfficiencyUpgradeCostText());

        MaxUpgradeBackpack = (int) Math.round((upgrade.getBackpacksize() * 100.0) / Math.max(1, upgrade.MaxUpgradeBackpack()));
        pb_upgrade_4.setProgress(MaxUpgradeBackpack);
        iv_upgrade_4_image.setImageResource(upgrade.getBackpackImage());
        et_upgrade_4_cost.setText(upgrade.getbackpackUpgradeCostText());




    }
}
