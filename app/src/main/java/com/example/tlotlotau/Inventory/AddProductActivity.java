package com.example.tlotlotau.Inventory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {
    private EditText etProductName, etProductPrice, etProductQuantity, etProductDescription;
    private ImageView ivQRCode;
    private Button btnGenerateQRCode, btnSaveProduct;
    private ImageButton btnBack;
    private Spinner spinnerCategories;
    private List<CategoryC> categories;
    private CategoryC selectedCategory;

    private DatabaseHelper dbHelper;
    private Bitmap generatedQRBitmap;
    private String generatedQRContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setClickListeners();

        loadCategoriesIntoSpinner();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductQuantity = findViewById(R.id.etProductQuantity);
        etProductDescription = findViewById(R.id.etProductDescription);
        ivQRCode = findViewById(R.id.ivQRCode);
        btnGenerateQRCode = findViewById(R.id.btnGenerateQRCode);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setClickListeners() {
        btnGenerateQRCode.setOnClickListener(v -> generateQRCode());
        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void loadCategoriesIntoSpinner() {
        categories = dbHelper.getAllCategories();

        if (categories == null || categories.isEmpty()) {
            // disable spinner/save and prompt user
            spinnerCategories.setEnabled(false);
            btnSaveProduct.setEnabled(false);
            Toast.makeText(this, "No categories found. Add categories first.", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> names = new ArrayList<>();
        for (CategoryC c : categories) names.add(c.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        spinnerCategories.setEnabled(true);
        btnSaveProduct.setEnabled(true);
    }

    private boolean validateInput() {
        if (etProductName.getText().toString().trim().isEmpty() ||
                etProductPrice.getText().toString().trim().isEmpty() ||
                etProductQuantity.getText().toString().trim().isEmpty() ||
                etProductDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void generateQRCode() {
        if (etProductName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter product name first (used to generate QR content)", Toast.LENGTH_SHORT).show();
            return;
        }
        generatedQRContent = etProductName.getText().toString().trim() + "_" + System.currentTimeMillis();
        generatedQRBitmap = QRCodeHelper.generateQRCode(generatedQRContent, 300, 300);
        if (generatedQRBitmap != null) ivQRCode.setImageBitmap(generatedQRBitmap);
        else Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
    }

    private void saveProduct() {
        if (!validateInput() || generatedQRContent == null) {
            Toast.makeText(this, "Please fill in all fields and generate QR Code", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String name = etProductName.getText().toString().trim();
            double price = Double.parseDouble(etProductPrice.getText().toString().trim());
            int quantity = Integer.parseInt(etProductQuantity.getText().toString().trim());
            String description = etProductDescription.getText().toString().trim();
            Long categoryId = selectedCategory == null ? null : selectedCategory.getId();

            long result = dbHelper.insertProduct(name, price, quantity, generatedQRContent, categoryId);
            if (result != -1) {
                Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ManageProductsActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
        }
    }
}
