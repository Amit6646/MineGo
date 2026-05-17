package com.example.minego.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Gender;
import com.example.minego.models.User;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;
import com.example.minego.utils.Validator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    RadioGroup rgUserGender;
    RadioButton rbGenderMale, rbGenderFemale;
    String selectedUid;
    boolean isCurrentUser = false;
    User selectedUser;
    private EditText etUserUsername, etUserEmail, etUserPassword;
    private com.google.android.material.button.MaterialButton btnUpdateProfile, btnRemove, btnLogout;
    private TextView tvProfileTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedUid = getIntent().getStringExtra("USER_UID");
        User currentUser = SharedPreferencesUtil.getUser(this);
        if (currentUser == null) {
            finish();
            return;
        }

        if (selectedUid == null) {
            selectedUid = currentUser.getId();
        }

        isCurrentUser = selectedUid.equals(currentUser.getId());
        if (!isCurrentUser && !currentUser.isAdmin()) {
            Toast.makeText(this, "You are not authorized to view this profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProfileTitle = findViewById(R.id.tv_user_profile_title);
        etUserUsername = findViewById(R.id.et_user_username);
        etUserEmail = findViewById(R.id.et_user_email);
        etUserPassword = findViewById(R.id.et_user_password);
        btnUpdateProfile = findViewById(R.id.btn_edit_profile);
        btnRemove = findViewById(R.id.btn_remove);
        btnLogout = findViewById(R.id.btn_profile_logout);

        if (tvProfileTitle != null) {
            tvProfileTitle.setText(isCurrentUser ? "Edit profile" : "Edit user");
        }
        if (btnUpdateProfile != null) {
            btnUpdateProfile.setText(isCurrentUser ? "Save profile" : "Update user");
        }

        rgUserGender = findViewById(R.id.rg_user_profile_gender);
        rbGenderFemale = findViewById(R.id.rb_user_profile_gender_female);
        rbGenderMale = findViewById(R.id.rb_user_profile_gender_male);

        DatabaseService.getInstance().getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (isCurrentUser && user != null) {
                    SharedPreferencesUtil.saveUser(UserProfileActivity.this, user);
                }
                showUserDetail(user);
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

        boolean canRemove = currentUser.isAdmin() && !isCurrentUser;
        btnRemove.setVisibility(canRemove ? View.VISIBLE : View.GONE);
        if (canRemove) {
            btnRemove.setOnClickListener(v -> confirmAndRemoveUser(selectedUid));
        }

        btnLogout.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);
        if (isCurrentUser) {
            btnLogout.setOnClickListener(v -> logout());
        }

        btnUpdateProfile.setOnClickListener(v -> updateUser());
    }

    private void logout() {
        SharedPreferencesUtil.signOutUser(this);
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void confirmAndRemoveUser(String uidToRemove) {
        if (uidToRemove == null || uidToRemove.isEmpty()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Remove user")
                .setMessage("Are you sure you want to remove this user? This action cannot be undone.")
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .setPositiveButton("Remove", (d, which) ->
                        DatabaseService.getInstance().deleteUser(uidToRemove, new DatabaseService.DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void object) {
                                Toast.makeText(UserProfileActivity.this, "User removed", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailed(Exception e) {
                                Toast.makeText(UserProfileActivity.this, "Failed to remove user", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .show();
    }

    private void showUserDetail(User user) {
        if (user == null) {
            return;
        }
        this.selectedUser = user;
        etUserUsername.setText(user.getUsername());
        etUserEmail.setText(user.getEmail());
        etUserPassword.setText("");
        if (user.getGender() == Gender.Male) {
            rgUserGender.check(rbGenderMale.getId());
        } else {
            rgUserGender.check(rbGenderFemale.getId());
        }
    }

    private void updateUser() {
        if (selectedUser == null) {
            return;
        }
        final String username = etUserUsername.getText().toString();
        final String password = etUserPassword.getText().toString();
        final String email = etUserEmail.getText().toString();

        Gender gender;
        if (rgUserGender.getCheckedRadioButtonId() == rbGenderMale.getId()) {
            gender = Gender.Male;
        } else {
            gender = Gender.Female;
        }

        if (!checkInput(username, password, email)) {
            return;
        }

        DatabaseService.getInstance().getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                for (User user : users) {
                    if (user.getId().equals(selectedUid)) {
                        continue;
                    }
                    if (user.getUsername().equals(username)) {
                        etUserUsername.setError("Username already taken");
                        etUserUsername.requestFocus();
                        return;
                    }
                    if (user.getEmail().equals(email)) {
                        etUserEmail.setError("Email already taken");
                        etUserEmail.requestFocus();
                        return;
                    }
                }

                selectedUser.setUsername(username);
                if (password != null && !password.isEmpty()) {
                    selectedUser.setPassword(password);
                }
                selectedUser.setEmail(email);
                selectedUser.setGender(gender);

                writeUser(selectedUser);
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    private void writeUser(@NotNull final User user) {
        DatabaseService.getInstance().writeUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                User current = SharedPreferencesUtil.getUser(UserProfileActivity.this);
                if (current != null && current.getId() != null && current.getId().equals(user.getId())) {
                    SharedPreferencesUtil.saveUser(UserProfileActivity.this, user);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UserProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkInput(String username, String password, String email) {
        if (!Validator.isNameValid(username)) {
            etUserUsername.setError("Username must be at least 3 characters long");
            etUserUsername.requestFocus();
            return false;
        }

        if (!Validator.isEmailValid(email)) {
            etUserEmail.setError("Email is not valid");
            etUserEmail.requestFocus();
            return false;
        }

        if (password != null && !password.isEmpty() && !Validator.isPasswordValid(password)) {
            etUserPassword.setError("Password must be at least 6 characters long");
            etUserPassword.requestFocus();
            return false;
        }

        if (!Validator.thisEnglish(username)) {
            etUserUsername.setError("Name must contain only English letters and underscores");
            etUserUsername.requestFocus();
            return false;
        }

        if (rgUserGender.getCheckedRadioButtonId() == View.NO_ID) {
            rgUserGender.requestFocus();
        }

        return true;
    }
}
