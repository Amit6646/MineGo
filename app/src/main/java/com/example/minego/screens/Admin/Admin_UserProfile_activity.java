package com.example.minego.screens.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class Admin_UserProfile_activity extends AppCompatActivity {

    private final String TAG = "Admin_UserProfile_activity";
    RadioGroup rgUserGender;
    RadioButton rbGenderMale, rbGenderFemale;
    String selectedUid;
    boolean isCurrentUser = false;
    User selectedUser;
    private EditText etUserUsername, etUserEmail, etUserPassword;
    private Button btnUpdateProfile, btnRemove;
    private View adminBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedUid = getIntent().getStringExtra("USER_UID");
        User currentUser = SharedPreferencesUtil.getUser(this);
        assert currentUser != null;

        if (selectedUid == null) {
            selectedUid = currentUser.getId();
        }

        isCurrentUser = selectedUid.equals(currentUser.getId());
        if (!isCurrentUser && !currentUser.isAdmin()) {
            // If the user is not an admin and the selected user is not the current user
            // then finish the activity
            Toast.makeText(this, "You are not authorized to view this profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        etUserUsername = findViewById(R.id.et_user_username);
        etUserEmail = findViewById(R.id.et_user_email);
        etUserPassword = findViewById(R.id.et_user_password);
        btnUpdateProfile = findViewById(R.id.btn_edit_profile);
        btnRemove = findViewById(R.id.btn_remove);
        rgUserGender = findViewById(R.id.rg_user_profile_gender);
        rbGenderFemale = findViewById(R.id.rb_user_profile_gender_female);
        rbGenderMale = findViewById(R.id.rb_user_profile_gender_male);
        DatabaseService.getInstance().getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (isCurrentUser) {
                    SharedPreferencesUtil.saveUser(Admin_UserProfile_activity.this, user);
                }
                showUserDetail(user);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        // Only admins can remove users, and never remove themselves
        boolean canRemove = currentUser.isAdmin() && !isCurrentUser;
        btnRemove.setVisibility(canRemove ? View.VISIBLE : View.GONE);
        if (canRemove) {
            btnRemove.setOnClickListener(v -> confirmAndRemoveUser(selectedUid));
        }

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUser();
            }
        });

    }

    private void confirmAndRemoveUser(String uidToRemove) {
        if (uidToRemove == null || uidToRemove.isEmpty()) return;
        new AlertDialog.Builder(this)
                .setTitle("Remove user")
                .setMessage("Are you sure you want to remove this user? This action cannot be undone.")
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .setPositiveButton("Remove", (d, which) -> {
                    DatabaseService.getInstance().deleteUser(uidToRemove, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            Toast.makeText(Admin_UserProfile_activity.this, "User removed", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(Admin_UserProfile_activity.this, "Failed to remove user", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }

    private void showUserDetail(User user) {
        this.selectedUser = user;
        etUserUsername.setText(user.getUsername());
        etUserEmail.setText(user.getEmail());
        // Don't prefill password for security; allow leaving blank to keep current password
        etUserPassword.setText("");
        if (user.getGender() == Gender.Male) {
            rgUserGender.check(rbGenderMale.getId());
        } else {
            rgUserGender.check(rbGenderFemale.getId());
        }

    }

    private void UpdateUser() {
        if (selectedUser == null) return;
        final String username = etUserUsername.getText().toString();
        final String password = etUserPassword.getText().toString();
        final String email = etUserEmail.getText().toString();

        Gender gender;
        if (rgUserGender.getCheckedRadioButtonId() == rbGenderMale.getId()) {
            gender = Gender.Male;
        } else //if (rgGender.getCheckedRadioButtonId() == rbGenderFemale.getId())
        {
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
                        /// show error message to user
                        etUserUsername.setError("Username already taken");
                        /// set focus to field
                        etUserUsername.requestFocus();
                        return;
                    }
                    if (user.getEmail().equals(email)) {
                        /// show error message to user
                        etUserEmail.setError("Email already taken");
                        /// set focus to field
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
                Toast.makeText(Admin_UserProfile_activity.this, "המשתמש עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                // If the current user edited themselves, keep local session in sync
                User current = SharedPreferencesUtil.getUser(Admin_UserProfile_activity.this);
                if (current != null && current.getId() != null && current.getId().equals(user.getId())) {
                    SharedPreferencesUtil.saveUser(Admin_UserProfile_activity.this, user);
                }

            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Admin_UserProfile_activity.this, "שמירה נכשלה", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean checkInput(String username, String password, String Email) {

        if (!Validator.isNameValid(username)) {
            /// show error message to user
            etUserUsername.setError("Username must be at least 3 characters long");
            /// set focus to UserName field
            etUserUsername.requestFocus();
            return false;
        }

        if (!Validator.isEmailValid(Email)) {
            /// show error message to user
            etUserEmail.setError("Email is not valid");
            /// set focus to password field
            etUserEmail.requestFocus();
            return false;
        }

        // Password is optional: empty means "keep current password"
        if (password != null && !password.isEmpty() && !Validator.isPasswordValid(password)) {
            etUserPassword.setError("Password must be at least 6 characters long");
            etUserPassword.requestFocus();
            return false;
        }

        if (!Validator.thisEnglish(username)) {
            /// show error message to user
            etUserUsername.setError("Name must contain only English letters and underscores");
            /// set focus to password field
            etUserUsername.requestFocus();
            return false;
        }

        if (rgUserGender.getCheckedRadioButtonId() == View.NO_ID) {
            rgUserGender.requestFocus();
        }

        return true;

    }
}
