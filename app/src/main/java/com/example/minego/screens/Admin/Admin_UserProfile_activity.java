package com.example.minego.screens.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.R;
import com.example.minego.models.Gender;
import com.example.minego.models.User;
import com.example.minego.screens.RegisterActivity;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;
import com.example.minego.utils.Validator;

import java.util.HashMap;

public class Admin_UserProfile_activity extends AppCompatActivity {

    private EditText etUserUsername, etUserEmail, etUserPassword;

    RadioGroup rgUserGender;
    RadioButton rbGenderMale, rbGenderFemale;
    private Button btnUpdateProfile, btnRemove;
    String selectedUid;
    private View adminBadge;
    boolean isCurrentUser = false;


    User selectedUser;


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
                if(isCurrentUser) {
                    SharedPreferencesUtil.saveUser(Admin_UserProfile_activity.this, user);
                }
                showUserDetail(user);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });


        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUser();
            }
        });

    }
    
    private void showUserDetail(User user) {
        this.selectedUser = user;
        etUserUsername.setText(user.getUsername());
        etUserEmail.setText(user.getEmail());
        etUserPassword.setText(user.getPassword());
        if (user.getGender() == Gender.Male) {
            rgUserGender.check(rbGenderMale.getId());
        } else {
            rgUserGender.check(rbGenderFemale.getId());
        }
        
    }

    private void UpdateUser() {
        if (selectedUser == null) return;

        if (!checkInput(etUserUsername.getText().toString(), etUserPassword.getText().toString(), etUserEmail.getText().toString())) return;

        Gender gender;
        if (rgUserGender.getCheckedRadioButtonId() == rbGenderMale.getId()) {
            gender = Gender.Male;
        } else //if (rgGender.getCheckedRadioButtonId() == rbGenderFemale.getId())
        {
            gender = Gender.Female;
        }


        selectedUser.setPassword(etUserPassword.getText().toString());
        selectedUser.setGender(gender);

        if(!checkInput(etUserUsername.getText().toString(), etUserPassword.getText().toString(),etUserEmail.getText().toString()))
        {
            return;
        }
        else if (!etUserUsername.getText().toString().equals(selectedUser.getUsername()))
        {
            selectedUser.setUsername(etUserUsername.getText().toString());
            checkusernameexits();
        }
        else if (!etUserEmail.getText().toString().equals(selectedUser.getEmail()))
        {
            selectedUser.setEmail(etUserEmail.getText().toString());
            checkEmailexits();
        }
        else
        {
            writeUser();
        }



        // TODO get all info from edit text
        // save in selectedUser
        // save in db
    }
    private void checkusernameexits(){
        DatabaseService.getInstance().checkIfUsernameExists(etUserUsername.getText().toString(), new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exist) {
                if (exist)
                {
                    Toast.makeText(Admin_UserProfile_activity.this, "Username already exists", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if (!etUserEmail.getText().toString().equals(selectedUser.getEmail()))
                    {
                        selectedUser.setEmail(etUserEmail.getText().toString());
                        checkEmailexits();
                    }
                    else
                    {
                        writeUser();
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
    private void checkEmailexits(){
        DatabaseService.getInstance().checkIfEmailExists(etUserEmail.getText().toString(), new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exist) {
                if (exist)
                {
                    Toast.makeText(Admin_UserProfile_activity.this, "email already exists", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    writeUser();
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void writeUser()
    {
        DatabaseService.getInstance().writeUser(selectedUser, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

            }

            @Override
            public void onFailed(Exception e) {

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

        if (!Validator.isPasswordValid(password)) {
            /// show error message to user
            etUserPassword.setError("Password must be at least 6 characters long");
            /// set focus to password field
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
