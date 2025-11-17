package com.example.tlotlotau.Inventory;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CategoryManagerActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private RecyclerView rvCategories;
    private FloatingActionButton fabAddCategory;
    private CategoryManagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manager);

        db = new DatabaseHelper(this);

        rvCategories = findViewById(R.id.rvCategories);
        fabAddCategory = findViewById(R.id.fabAddCategory);

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        load();

        fabAddCategory.setOnClickListener(v -> showAddDialog());
    }

    private void load() {
        List<CategoryC> cats = db.getAllCategories();
        adapter = new CategoryManagerAdapter(cats, c -> showEditDialog(c));
        rvCategories.setAdapter(adapter);
    }

    /**
     * Shows the "Add Category" dialog with app colors applied programmatically.
     */
    private void showAddDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setHint("Category name");

        // Colors from resources (ensure these exist in colors.xml)
        int colorWhite = ContextCompat.getColor(this, R.color.white);
        int colorBlack = ContextCompat.getColor(this, R.color.black);
        int colorGrey = ContextCompat.getColor(this, R.color.grey);
        int colorGold = ContextCompat.getColor(this, R.color.gold);

        // style input
        input.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey)); // light background
        input.setPadding(32, 24, 32, 24);
        input.setTextColor(colorBlack);
        input.setHintTextColor(colorGrey);

        // Title with colored text
        SpannableString titleSpan = new SpannableString("Add Category");
        titleSpan.setSpan(new ForegroundColorSpan(colorBlack), 0, titleSpan.length(), 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleSpan);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText() == null ? "" : input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = db.insertCategory(name);
            if (id != -1) {
                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                load();
            } else {
                Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        // Apply window background color and show before styling buttons
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(colorWhite));
        }

        dialog.setOnShowListener(dialogInterface -> {
            // Positive button color
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorGold);
            // Negative button color
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorBlack);
        });

        dialog.show();
    }

    /**
     * Shows the "Edit Category" dialog, styled programmatically, with Delete option.
     */
    private void showEditDialog(CategoryC c) {
        if (c == null) return;

        final EditText input = new EditText(this);
        input.setText(c.getName());
        input.setSelection(c.getName() != null ? c.getName().length() : 0);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Colors
        int colorWhite = ContextCompat.getColor(this, R.color.white);
        int colorBlack = ContextCompat.getColor(this, R.color.black);
        int colorGrey = ContextCompat.getColor(this, R.color.grey);
        int colorGold = ContextCompat.getColor(this, R.color.gold);

        // style input
        input.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey));
        input.setPadding(32, 24, 32, 24);
        input.setTextColor(colorBlack);
        input.setHintTextColor(colorGrey);

        // Title span
        SpannableString titleSpan = new SpannableString("Edit category");
        titleSpan.setSpan(new ForegroundColorSpan(colorBlack), 0, titleSpan.length(), 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleSpan);
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText() == null ? "" : input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            int rows = db.updateCategory(c.getId(), newName);
            if (rows > 0) {
                Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show();
                load();
            } else {
                Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("Delete", null); // we'll intercept in onShow
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        // set background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(colorWhite));
        }

        dialog.setOnShowListener(dialogInterface -> {
            // style buttons
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorGold);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorBlack);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(colorBlack);

            // override neutral (Delete) click to show confirmation dialog
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                // Confirm delete
                SpannableString delTitle = new SpannableString("Delete category");
                delTitle.setSpan(new ForegroundColorSpan(colorBlack), 0, delTitle.length(), 0);

                new AlertDialog.Builder(this)
                        .setTitle(delTitle)
                        .setMessage("Are you sure you want to delete \"" + c.getName() + "\"? Products in this category will be unassigned.")
                        .setPositiveButton("Delete", (d, w) -> {
                            int rows = db.deleteCategory(c.getId());
                            if (rows > 0) {
                                Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                                load();
                                dialog.dismiss(); // close parent edit dialog
                            } else {
                                Toast.makeText(this, "Failed to delete category", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }
}
