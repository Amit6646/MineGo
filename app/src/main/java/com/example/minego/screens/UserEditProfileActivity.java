package com.example.minego.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.User;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;
import com.example.minego.utils.Validator;

public class UserEditProfileActivity extends AppCompatActivity {

    EditText etPassword, etPasswordOriginal;
    Button btnSave;
    User user;
    DatabaseService databaseService;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = SharedPreferencesUtil.getUser(this);
        databaseService = DatabaseService.getInstance();
        etPassword = findViewById(R.id.et_editprofile_password);
        etPasswordOriginal = findViewById(R.id.et_editprofile_original_password);
        btnSave = findViewById(R.id.btn_save_profile);
        id = user.getId();
        btnSave.setOnClickListener(v -> Update());


    }
    private boolean Update()
    {
        if (!user.getPassword().equals(etPasswordOriginal.getText().toString() + "")) {
            return false;
        }

        if(!Validator.isPasswordValid(etPassword.getText().toString())){
            return false;
        }
        user.setPassword(etPassword.getText().toString());
        databaseService.getUser(id, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                user.setPassword(etPassword.getText().toString());
                databaseService.writeUser(user, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {

                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        return true;
    }



}