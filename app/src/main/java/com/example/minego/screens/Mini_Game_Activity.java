package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;

import com.example.minego.models.Upgrade;
import com.example.minego.models.User;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;

import java.util.ArrayList;

public class Mini_Game_Activity extends AppCompatActivity {

    private int MineHp;
    //private Upgrade upgrade;
    private ImageButton btn_clicker;
    private TextView tv_hp;
    private User user;
    private int Minelevel = 0;

    private int StartHP;
    private int imgStage = 1;
    private int[] mineImages = {
            R.drawable.mine1,
            R.drawable.mine2,
            R.drawable.mine3,
            R.drawable.mine4,
            R.drawable.mine5,
            R.drawable.mine6,
            R.drawable.mine7,
            R.drawable.mine8,
            R.drawable.mine9,
            R.drawable.mine10,
            R.drawable.mine11,
            R.drawable.mine12,
            R.drawable.mine13,
            R.drawable.mine14,
            R.drawable.mine15
    };

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
        user = SharedPreferencesUtil.getUser(this);
        Upgrade upgrade = user.getUpgrade();
        Minelevel = upgrade.getMineDrop();
        MineHp = upgrade.getMineHp();
        StartHP = MineHp;

        btn_clicker = findViewById(R.id.btn_minigame_temp);
        tv_hp = findViewById(R.id.tv_minigame_temp);
        tv_hp.setText("Hp: " + MineHp);

        btn_clicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MineHp -= 1;
                GetImgLevel(MineHp);
                tv_hp.setText("Hp: " + MineHp);
                if (MineHp == 0)
                {

                }
            }
        });

    }
    private void GetImgLevel(int hp)
    {
        int img = StartHP / 15;

        if ((hp % img) == 0)
        {
            imgStage++;
            updateMineImage(imgStage);

        }




    }

    private void updateMineImage(int numimg) {
        if(numimg < 1){
            numimg = 1;
        }
        if(numimg > mineImages.length)
        {
            numimg = mineImages.length;
        }

        btn_clicker.setImageResource(mineImages[numimg - 1]);
    }
    private void GetRewards(){

    }

}