package com.example.minego.screens.Admin;



import android.content.Intent;

import android.os.Bundle;

import android.widget.Button;



import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;

import androidx.core.view.WindowInsetsCompat;



import com.example.minego.R;

import com.example.minego.screens.Mini_Game_Activity;



public class Admin_landing_Activity extends AppCompatActivity {

    Button UserList, MinerEdit, JoinGame;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_admin_landing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;

        });

        // מקשר את הכפתור לכפתור העיצובי
        UserList = findViewById(R.id.btn_admin_lan_userList);

        // מגדיר שכאשר לוחצים על הכפתור זה מעביר למסך של רשימית המשתמשים
        UserList.setOnClickListener(v -> startActivity(new Intent(this, AdminUserListActivity.class)));

        // מקשר את הכפתור לכפתור העיצובי
        MinerEdit = findViewById(R.id.btn_admin_lan_Miners_list);

        // מגדיר שכאשר לוחצים על הכפתור זה מעביר למסך למסך של רשימת המכרות
        MinerEdit.setOnClickListener(v -> startActivity(new Intent(this, AdminMineListActivity.class)));

        // מקשר את הכפתור לכפתור העיצובי
        JoinGame = findViewById(R.id.btn_admin_join_minigames);

        // מגדיר שכאשר לוחצים על הכפתור זה מעביר למסך של המינימשחקון
        JoinGame.setOnClickListener(v -> startActivity(new Intent(this, Mini_Game_Activity.class)));

    }

}


