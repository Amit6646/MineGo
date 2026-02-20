package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Upgrade;

public class Mini_Game_Activity extends AppCompatActivity {

    private int hp = 10;
    private Upgrade upgrade;

    private Button btn_clicker;
    private TextView tv_hp;
    private int level = upgrade.getMineLevel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mini_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn_clicker = findViewById(R.id.btn_minigame_temp);
        tv_hp = findViewById(R.id.tv_minigame_temp);
        tv_hp.setText("Hp: " + hp);

        btn_clicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hp = hp - 1;
                tv_hp.setText("Hp: " + hp);
            }
        });

    }

}