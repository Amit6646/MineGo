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
import com.example.minego.screens.UserEditProfileActivity;

public class Admin_landing_Activity extends AppCompatActivity {
    Button UserList, MinerEdit;
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
        UserList = findViewById(R.id.btn_admin_lan_userList);
        UserList.setOnClickListener(v -> startActivity(new Intent(this, AdminUserListActivity.class)));


        MinerEdit = findViewById(R.id.btn_admin_lan_Miners_list);
        MinerEdit.setOnClickListener(v -> startActivity(new Intent(this, AdminActivity.class)));

    }
}