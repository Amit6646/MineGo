package com.example.minego;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minego.models.Backpack;
import com.example.minego.models.Item;
import com.example.minego.models.ItemType;
import com.example.minego.models.User;
import com.example.minego.services.DatabaseService;
import com.example.minego.utils.SharedPreferencesUtil;

import java.util.List;

public class backpack extends AppCompatActivity {

    private TextView tvCapacity;
    private TextView tvEmpty;
    private ProgressBar pbFill;
    private LinearLayout llItems;

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

        tvCapacity = findViewById(R.id.tv_backpack_capacity);
        tvEmpty = findViewById(R.id.tv_backpack_empty);
        pbFill = findViewById(R.id.pb_backpack_fill);
        llItems = findViewById(R.id.ll_backpack_items);

        bindBackpackUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindBackpackUi();
    }

    private void showRemoveAmountDialog(ItemType type, int availableCount) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(R.string.backpack_delete_hint);
        input.setText("1");
        input.setSelectAllOnFocus(true);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad / 2, pad, pad / 2);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.backpack_delete_title)
                .setMessage(getString(R.string.backpack_delete_message, type.getType(), availableCount))
                .setView(input)
                .setPositiveButton(R.string.backpack_delete_confirm, null)
                .setNegativeButton(R.string.backpack_delete_cancel, (d, which) -> {
                })
                .create();

        dialog.setOnShowListener(d -> {
            Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            ok.setOnClickListener(v -> {
                String raw = input.getText().toString().trim();
                int amount;
                try {
                    amount = Integer.parseInt(raw);
                } catch (NumberFormatException ex) {
                    Toast.makeText(this,
                            getString(R.string.backpack_delete_invalid_amount, availableCount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount < 1 || amount > availableCount) {
                    Toast.makeText(this,
                            getString(R.string.backpack_delete_invalid_amount, availableCount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                syncRemoveFromBackpack(type, amount);
            });
        });

        dialog.show();
    }

    private void syncRemoveFromBackpack(ItemType type, int amount) {
        User local = SharedPreferencesUtil.getUser(this);
        if (local == null || local.getId() == null || local.getId().isEmpty()) {
            Toast.makeText(this, R.string.backpack_delete_no_user, Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseService.getInstance().updateUser(local.getId(), currentUser -> {
            if (currentUser == null) {
                return null;
            }
            Backpack bp = currentUser.getBackpack();
            if (bp == null) {
                return currentUser;
            }
            bp.removeItem(new Item(type, amount));
            return currentUser;
        }, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User updatedUser) {
                if (updatedUser != null) {
                    SharedPreferencesUtil.saveUser(backpack.this, updatedUser);
                }
                bindBackpackUi();
            }

            @Override
            public void onFailed(Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : e.toString();
                Toast.makeText(backpack.this,
                        getString(R.string.backpack_delete_sync_failed, msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindBackpackUi() {
        User user = SharedPreferencesUtil.getUser(this);
        if (user != null) {
            user.syncBackpackCapacityFromUpgrade();
            SharedPreferencesUtil.saveUser(this, user);
        }
        Backpack bp = user != null ? user.getBackpack() : null;
        int used = bp != null ? bp.currentSize() : 0;
        int cap = bp != null ? bp.getTotalSize() : 0;

        if (cap > 0) {
            tvCapacity.setText(getString(R.string.backpack_capacity_format, used, cap));
            pbFill.setMax(cap);
            pbFill.setProgress(Math.min(used, cap));
        } else {
            tvCapacity.setText(getString(R.string.backpack_capacity_no_limit, used));
            pbFill.setMax(100);
            pbFill.setProgress(0);
        }

        llItems.removeAllViews();
        List<Item> items = bp != null ? bp.getItems() : null;

        LayoutInflater inflater = LayoutInflater.from(this);
        int rowCount = 0;
        if (items != null) {
            for (Item item : items) {
                if (item == null || item.getType() == null || item.getCount() <= 0) {
                    continue;
                }
                View row = inflater.inflate(R.layout.item_backpack_row, llItems, false);
                TextView name = row.findViewById(R.id.tv_backpack_item_name);
                TextView count = row.findViewById(R.id.tv_backpack_item_count);
                ImageButton deleteBtn = row.findViewById(R.id.btn_backpack_item_delete);
                name.setText(item.getType().getType());
                count.setText(getString(R.string.backpack_item_count_format, item.getCount()));
                ItemType rowType = item.getType();
                int rowCountAvailable = item.getCount();
                deleteBtn.setOnClickListener(v ->
                        showRemoveAmountDialog(rowType, rowCountAvailable));
                llItems.addView(row);
                rowCount++;
            }
        }

        tvEmpty.setVisibility(rowCount == 0 ? View.VISIBLE : View.GONE);
    }
}
