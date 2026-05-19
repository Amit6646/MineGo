package com.example.minego.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minego.R;
import com.example.minego.models.Item;
import com.example.minego.models.ItemType;

import java.util.ArrayList;
import java.util.List;

public class BackpackAdapter extends RecyclerView.Adapter<BackpackAdapter.ViewHolder> {

    private final List<Item> itemList;
    private final OnItemDeleteListener onItemDeleteListener;

    public BackpackAdapter(@Nullable OnItemDeleteListener onItemDeleteListener) {
        itemList = new ArrayList<>();
        this.onItemDeleteListener = onItemDeleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_backpack_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        if (item == null || item.getType() == null) {
            return;
        }

        holder.tv_item_name.setText(item.getType().getType());
        holder.tv_item_count.setText(holder.itemView.getContext().getString(
                R.string.backpack_item_count_format, item.getCount()));

        ItemType itemType = item.getType();
        int itemCount = item.getCount();
        holder.btn_item_delete.setOnClickListener(v -> {
            if (onItemDeleteListener != null) {
                onItemDeleteListener.onDeleteClick(itemType, itemCount);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<Item> items) {
        itemList.clear();
        if (items != null) {
            for (Item item : items) {
                if (item == null || item.getType() == null || item.getCount() <= 0) {
                    continue;
                }
                itemList.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public interface OnItemDeleteListener {
        void onDeleteClick(ItemType type, int maxAmount);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name, tv_item_count;
        ImageButton btn_item_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_name = itemView.findViewById(R.id.tv_backpack_item_name);
            tv_item_count = itemView.findViewById(R.id.tv_backpack_item_count);
            btn_item_delete = itemView.findViewById(R.id.btn_backpack_item_delete);
        }
    }
}
