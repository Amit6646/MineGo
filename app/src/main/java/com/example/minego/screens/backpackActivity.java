package com.example.minego.screens;



import android.os.Bundle;

import android.text.InputType;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ProgressBar;

import android.widget.TextView;

import android.widget.Toast;



import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;

import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;



import com.example.minego.R;

import com.example.minego.adapters.BackpackAdapter;

import com.example.minego.models.Backpack;

import com.example.minego.models.Item;

import com.example.minego.models.ItemType;

import com.example.minego.models.User;

import com.example.minego.services.DatabaseService;

import com.example.minego.utils.SharedPreferencesUtil;



import java.util.List;



public class backpackActivity extends AppCompatActivity {



    TextView tv_backpack_capacity, tv_backpack_empty;

    ProgressBar pb_backpack_fill;

    RecyclerView rv_backpack_items;

    BackpackAdapter backpackAdapter;



    User user;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_backpack);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        user = SharedPreferencesUtil.getUser(this);



        tv_backpack_capacity = findViewById(R.id.tv_backpack_capacity);
        tv_backpack_empty = findViewById(R.id.tv_backpack_empty);
        pb_backpack_fill = findViewById(R.id.pb_backpack_fill);
        rv_backpack_items = findViewById(R.id.rv_backpack_items);

        rv_backpack_items.setLayoutManager(new LinearLayoutManager(this));
        rv_backpack_items.setNestedScrollingEnabled(false);

        backpackAdapter = new BackpackAdapter((type, maxAmount) -> showDeleteDialog(type, maxAmount));
        rv_backpack_items.setAdapter(backpackAdapter);

        updateBackpackUi();

    }



    @Override

    protected void onResume() {

        super.onResume();

        // טוען מחדש את המשתמש בכל כניסה למסך — אחרי מיני־גיים או שדרוג תיק
        if (user == null) {
            finish();
            return;
        }

        user.syncBackpackCapacityFromUpgrade();
        SharedPreferencesUtil.saveUser(this, user);
        updateBackpackUi();

    }



    private void updateBackpackUi() {

        if (user != null) {
            user.syncBackpackCapacityFromUpgrade();
            SharedPreferencesUtil.saveUser(this, user);

        }



        Backpack bp = null;
        if (user != null) {
            bp = user.getBackpack();
        }



        // כמה פריטים יש עכשיו בתיק

        int used;
        if (bp != null) {
            used = bp.currentSize();
        } else {
            used = 0;
        }



        // מקסימום פריטים מותר (לפי שדרוג)

        int cap;
        if (bp != null) {
            cap = bp.getTotalSize();
        } else {
            cap = 0;
        }



        if (cap > 0) {

            tv_backpack_capacity.setText(getString(R.string.backpack_capacity_format, used, cap));
            pb_backpack_fill.setMax(cap);
            pb_backpack_fill.setProgress(Math.min(used, cap));

        }
        else {

            tv_backpack_capacity.setText(getString(R.string.backpack_capacity_no_limit, used));
            pb_backpack_fill.setMax(100);
            pb_backpack_fill.setProgress(0);

        }



        List<Item> items = null;

        if (bp != null) {
            items = bp.getItems();
        }

        backpackAdapter.setItemList(items);



        // מציג הודעה כשאין פריטים בתיק

        if (backpackAdapter.getItemCount() == 0) {
            tv_backpack_empty.setVisibility(View.VISIBLE);
        }
        else {
            tv_backpack_empty.setVisibility(View.GONE);
        }
    }



    private void showDeleteDialog(ItemType type, int maxAmount) {

        final EditText et_delete_amount = new EditText(this);
        et_delete_amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_delete_amount.setHint(R.string.backpack_delete_hint);
        et_delete_amount.setText("1");
        et_delete_amount.setSelectAllOnFocus(true);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        et_delete_amount.setPadding(pad, pad / 2, pad, pad / 2);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.backpack_delete_title)
                .setMessage(getString(R.string.backpack_delete_message, type.getType(), maxAmount))
                .setView(et_delete_amount)
                .setPositiveButton(R.string.backpack_delete_confirm, null)
                .setNegativeButton(R.string.backpack_delete_cancel, (d, which) -> d.dismiss())
                .create();



        dialog.setOnShowListener(d -> {

            Button btn_confirm = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn_confirm.setOnClickListener(v -> {
                String raw = et_delete_amount.getText().toString().trim();
                int amount = parseDeleteAmount(raw);
                if (!checkDeleteAmount(amount, maxAmount)) {
                    return;
                }
                dialog.dismiss();
                removeFromBackpack(type, amount);

            });

        });

        dialog.show();

    }



    private int parseDeleteAmount(String raw) {
        if (raw == null) {
            return -1;
        }
        raw = raw.trim();

        if (raw.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < raw.length(); i++) {
            if (!Character.isDigit(raw.charAt(i))) {
                return -1;
            }
        }
        return Integer.parseInt(raw);
    }



    private boolean checkDeleteAmount(int amount, int maxAmount) {

        if (amount < 1 || amount > maxAmount) {
            Toast.makeText(this,
                    getString(R.string.backpack_delete_invalid_amount, maxAmount),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }



    private void removeFromBackpack(ItemType type, int amount) {
        //בודק שהמתשתמש תקין אחרת רושם הודעת שגיאה
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            Toast.makeText(this, R.string.backpack_delete_no_user, Toast.LENGTH_SHORT).show();
            return;
        }

        //מעדכן את הפרטים המדויקים ביותר מהמסד נתונים
        DatabaseService.getInstance().updateUser(user.getId(), currentUser -> {
            // יש בעיה במשתמש
            if (currentUser == null) {
                return null;
            }

            //במקרה שאין לשחקן תיק איך שהוא זה יותר לו תיק
            Backpack bp = currentUser.getBackpack();
            if (bp == null) {
                return currentUser;
            }

            //מוחק לו את הפריטים מהתיק גב ומעדכן אותם במסד נתונים
            bp.removeItem(new Item(type, amount));
            return currentUser;
        }, new DatabaseService.DatabaseCallback<User>() {

            @Override

            public void onCompleted(User updatedUser) {
                if (updatedUser != null) {
                    SharedPreferencesUtil.saveUser(backpackActivity.this, updatedUser);
                }
                updateBackpackUi();
            }



            @Override
            public void onFailed(Exception e) {
                String msg;
                if (e.getMessage() != null) {
                    msg = e.getMessage();
                } else {
                    msg = e.toString();
                }

                Toast.makeText(backpackActivity.this,
                        getString(R.string.backpack_delete_sync_failed, msg),
                        Toast.LENGTH_LONG).show();
            }

        });

    }

}

