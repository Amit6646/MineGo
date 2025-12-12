package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        btnSubmit = findViewById(R.id.btn_login_submit);

        btnSubmit.setOnClickListener(v -> Login());
    }

    private void Login() {

        String username = etUsername.getText().toString() + "";
        String password = etPassword.getText().toString() + "";

        if (!checkInput(username, password)) {
            return;
        }

        DatabaseService databaseService = DatabaseService.getInstance();
        databaseService.getUserByUsernameAndPassword(username, password, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user == null) {
                    // failed to find the user
                    etPassword.setError("Username or Password are invalid or User not exist");
                    etPassword.requestFocus();
                    return;
                }
                SharedPreferencesUtil.saveUser(LoginActivity.this, user);

                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                /// Clear the back stack (clear history) and start the MainActivity
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });


    }

    private boolean checkInput(String username, String password) {
        if (!Validator.isNameValid(username)) {
            etUsername.setError("Username is not valid");
            etUsername.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            etPassword.setError("Password is not valid");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}