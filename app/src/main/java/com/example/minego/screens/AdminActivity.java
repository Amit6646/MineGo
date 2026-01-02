package com.example.minego.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Miner;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.MapManager;
import com.example.minego.utils.behaviors.AdminPlacementBehavior;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class AdminActivity extends AppCompatActivity {
    private MapManager mapManager;
    private AdminPlacementBehavior adminBehavior;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. אתחול המפה והמנהל
        MapView map = findViewById(R.id.adminmap);
        mapManager = new MapManager(this, map);

        // 2. יצירת ההתנהגות (Behavior) של המנהל והזרקתה
        adminBehavior = new AdminPlacementBehavior(this, R.drawable.girlplayer);
        mapManager.setBehavior(adminBehavior);

        // 3. כפתור שמירה
        btnSave = findViewById(R.id.btn_admin_save);
        btnSave.setOnClickListener(v -> {
            // שואלים את ה-Behavior מה המיקום שנבחר
            GeoPoint pos = adminBehavior.getSelectedPosition();

            if (pos == null) {
                // אם עדיין לא נבחר מיקום
                Toast.makeText(AdminActivity.this, "אתה צריך לבחור מיקום בשביל ליצור מכרה", Toast.LENGTH_SHORT).show();
            } else {
                // יש מיקום - יוצרים מכרה
                String id = DatabaseService.getInstance().generateMinerId();
                Miner miner = new Miner(id, pos);
                createMinerInDatabase(miner);
            }
        });
    }

    private void createMinerInDatabase(Miner miner) {
        DatabaseService.getInstance().createNewMiner(miner, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(AdminActivity.this, "המכרה נוצר בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AdminActivity.this, "שגיאה ביצירת המכרה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapManager != null) {
            mapManager.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapManager != null) {
            mapManager.onPause();
        }
    }
}