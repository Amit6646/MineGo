package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Backpack;
import com.example.minego.models.Stats;
import com.example.minego.models.User;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;
import com.example.minego.utils.Validator;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etPasswordConfirm;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        etPasswordConfirm = findViewById(R.id.et_register_password_confirmation);

        btnSubmit = findViewById(R.id.btn_Register_submit);

        btnSubmit.setOnClickListener(v -> register());


    }
    private void register() {

        String username = etUsername.getText().toString() + "";
        String password = etPassword.getText().toString() + "";
        String passwordCofirm = etPasswordConfirm.getText().toString() + "";


        if (!checkInput(username, password, passwordCofirm)) {
            return;
        }

        registerUser(username, password);

    }

    private boolean checkInput(String username, String password, String passwordConfrim) {

        if (!Validator.isNameValid(username)) {
            /// show error message to user
            etUsername.setError("Username must be at least 3 characters long");
            /// set focus to email field
            etUsername.requestFocus();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            /// show error message to user
            etPassword.setError("Password must be at least 6 characters long");
            /// set focus to password field
            etPassword.requestFocus();
            return false;
        }

        if (!password.equals(passwordConfrim)) {
            /// show error message to user
            etPassword.setError("The password does not match the password confirmation.");
            /// set focus to password field
            etPassword.requestFocus();
            return false;
        }

        return true;

    }
    private void registerUser(String UserName, String password) {

        DatabaseService databaseService = DatabaseService.getInstance();
        String uid = databaseService.generateUserId();

        /// create a new user object
        User user = new User(uid, UserName, password, 0 , new Backpack(), new Stats(), new ArrayList<>(), false);

        databaseService.checkIfUsernameExists(user.username, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    /// show error message to user
                    Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    /// proceed to create the user
                    createUserInDatabase(user);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserInDatabase(User user) {
        DatabaseService databaseService = DatabaseService.getInstance();
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                /// save the user to shared preferences
                SharedPreferencesUtil.saveUser(RegisterActivity.this, user);
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                /// show error message to user
                Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register
                SharedPreferencesUtil.signOutUser(RegisterActivity.this);
            }
        });
    }

}