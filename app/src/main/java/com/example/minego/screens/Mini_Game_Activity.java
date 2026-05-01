package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;

import com.example.minego.models.Backpack;
import com.example.minego.models.Item;
import com.example.minego.models.ItemType;
import com.example.minego.models.Miner;
import com.example.minego.models.Upgrade;
import com.example.minego.models.User;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mini_Game_Activity extends AppCompatActivity {

    private int MineHp;
    //private Upgrade upgrade;
    private ImageButton btn_clicker;
    private TextView tv_hp;
    private User user;
    private int Minelevel = 0;

    private Miner miner;
    private int StartHP;
    private int imgStage = 1;
    /** מונע מהמכרה להגיע ל -1 HP */
    private boolean mineClearedHandled = false;
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
                if (MineHp <= 0) {
                    onMineDepleted();
                }
            }
        });

    }

    private void onMineDepleted() {
        if (mineClearedHandled) {
            return;
        }
        mineClearedHandled = true;
        btn_clicker.setEnabled(false);
        MineHp = 0;
        tv_hp.setText("Hp: 0");
        final Item rewards = miner.GetItemDrop();
        Toast.makeText(this, (rewards.getType() + " *" + rewards.getCount()) , Toast.LENGTH_LONG).show();


        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            Toast.makeText(this, "שגיאה במציאת השחקן", Toast.LENGTH_LONG).show();
            goToMainScreen();
            return;
        }

        DatabaseService.getInstance().updateUser(user.getId(), currentUser -> {
            if (currentUser == null) {
                return null;
            }
           // mergeRewardsIntoBackpack(currentUser, rewards);
            return currentUser;
        }, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User updatedUser) {
                if (updatedUser != null) {
                    SharedPreferencesUtil.saveUser(Mini_Game_Activity.this, updatedUser);
                }
                // Toast.makeText(Mini_Game_Activity.this, toastText, Toast.LENGTH_LONG).show();
                goToMainScreen();
            }

            @Override
            public void onFailed(Exception e) {
                User local = SharedPreferencesUtil.getUser(Mini_Game_Activity.this);
                if (local != null && user.getId().equals(local.getId())) {
                   // mergeRewardsIntoBackpack(local, rewards);
                    SharedPreferencesUtil.saveUser(Mini_Game_Activity.this, local);
                }
                //Toast.makeText(Mini_Game_Activity.this,
                //        toastText + "\n" + getString(R.string.minigame_sync_failed, e.getMessage() != null ? e.getMessage() : ""),
                //        Toast.LENGTH_LONG).show();
                goToMainScreen();
            }
        });
    }

    private void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }



    private void mergeRewardsIntoBackpack(User targetUser, List<Item> rewards) {
        Backpack bp = targetUser.getBackpack();
        if (bp == null) {
            bp = new Backpack();
            targetUser.setBackpack(bp);
        }
        int sumNew = 0;
        for (Item r : rewards) {
            sumNew += r.getCount();
        }
        int needed = bp.currentSize() + sumNew;
        if (bp.totalSize < needed) {
            bp.totalSize = needed + 100;
        }
        for (Item reward : rewards) {
            bp.addItem(new Item(reward.getType(), reward.getCount()));
        }
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

}