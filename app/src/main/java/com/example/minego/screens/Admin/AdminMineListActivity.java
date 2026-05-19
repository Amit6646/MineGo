package com.example.minego.screens.Admin;

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

public class AdminMineListActivity extends AppCompatActivity {
    Button btnSave;
    private MapManager mapManager;
    private AdminPlacementBehavior adminBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_mine_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MapView map = findViewById(R.id.adminmap);
        mapManager = new MapManager(this, map, new MapManager.OnMapManagerListener() {
            @Override
            public void onMinerClick(Miner miner) {

            }
        });

        adminBehavior = new AdminPlacementBehavior(this, org.osmdroid.library.R.drawable.marker_default);
        mapManager.setBehavior(adminBehavior);


        // מגדיר את הכפתור של השמירה שהיה מקושר לעיצוב
        btnSave = findViewById(R.id.btn_admin_save);
        // כאשר אני לוחץ על הכפתור
        btnSave.setOnClickListener(v -> {
            GeoPoint pos = adminBehavior.getSelectedPosition(); // בודק איפה לחצתי על המפה ושומר ב pos

            // במידה ולא לחצתי זה רושם הודעת שגיאה
            if (pos == null) {
                Toast.makeText(AdminMineListActivity.this, "אתה צריך לבחור מיקום בשביל ליצור מכרה", Toast.LENGTH_SHORT).show();
            } else {
                // עם רשמתי זה יוצר למכרה id
                String id = DatabaseService.getInstance().generateMinerId();
                // מגדיר מכרה לפי המיקום וה id
                Miner miner = new Miner(id, pos);
                // מוסיף את המכרה למסך נתוניפ
                createMinerInDatabase(miner);
            }
        });
    }

    private void createMinerInDatabase(Miner miner) {
        DatabaseService.getInstance().createNewMiner(miner, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(AdminMineListActivity.this, "המכרה נוצר בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AdminMineListActivity.this, "שגיאה ביצירת המכרה", Toast.LENGTH_SHORT).show();
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