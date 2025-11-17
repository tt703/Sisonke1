package com.example.tlotlotau.Inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.VH> {

    public interface OnClick {
        void onClick(CategoryC c);
    }

    // single internal mutable list (always non-null)
    private final List<CategoryC> categories;
    private final OnClick listener;
    private long selectedCategoryId = Long.MIN_VALUE;

    public CategoryAdapter2(List<CategoryC> initial, OnClick listener) {
        // defensive copy so we always have a mutable non-null list
        this.categories = (initial == null) ? new ArrayList<>() : new ArrayList<>(initial);
        this.listener = listener;
    }

    // safely replace contents
    public void updateData(List<CategoryC> newList) {
        categories.clear();
        if (newList != null && !newList.isEmpty()) categories.addAll(newList);
        notifyDataSetChanged();
    }

    public void setSelectedCategoryId(long id) {
        this.selectedCategoryId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item_chip, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CategoryC c = categories.get(position);
        holder.name.setText(c.getName() == null ? "" : c.getName());

        // visual selected state
        if (c.getId() == selectedCategoryId) {
            holder.itemView.setBackgroundResource(R.drawable.category_chip_selected);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.category_chip_background);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
