package com.example.tlotlotau.Employees;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String role);
    }

    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);

        holder.itemView.setOnClickListener(v -> {
            // deselect others
            for (Category c : categories) c.isSelected = false;
            category.isSelected = true;
            notifyDataSetChanged();

            if (listener != null) listener.onCategoryClick(category.name);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategory;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }

        void bind(Category category) {
            if (tvCategory == null) return; // defensive
            tvCategory.setText(category.name);
            if (category.isSelected) {
                tvCategory.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gold));
                tvCategory.setBackgroundResource(R.drawable.chip_selected_bg);
            } else {
                tvCategory.setTextColor(Color.DKGRAY);
                tvCategory.setBackgroundResource(R.drawable.chip_unselected_bg);
            }
        }
    }
}
