package com.example.tlotlotau.Inventory;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Added missing import
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;

public class EditProductActivity extends AppCompatActivity {

    private EditText etEditProductName, etEditProductPrice, etEditProductQuantity, etEditProductDescription;
    private Button btnUpdateProduct;
    private ImageButton btnBack; // Added missing view reference
    private DatabaseHelper dbHelper;
    private int productId;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_edit_product);

        dbHelper = new DatabaseHelper(this);

        // Initialize Views
        etEditProductName = findViewById(R.id.etEditProductName);
        etEditProductPrice = findViewById(R.id.etEditProductPrice);
        etEditProductQuantity = findViewById(R.id.etEditProductQuantity);
        etEditProductDescription = findViewById(R.id.etEditProductDescription);
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // FIX: Changed key from "Name" to "product" to match the Intent sender
        productId = getIntent().getIntExtra("product", -1);

        if (productId == -1) {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductData(); // Extracted loading logic for cleaner code

        btnUpdateProduct.setOnClickListener(v -> updateProduct());
    }

    private void loadProductData() {
        Product product = dbHelper.getProductById(productId);
        if (product != null) {
            etEditProductName.setText(product.getProductName());
            etEditProductPrice.setText(String.valueOf(product.getProductPrice()));
            etEditProductQuantity.setText(String.valueOf(product.getProductQuantity()));
            etEditProductDescription.setText(product.getProductDescription());
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateProduct() {
        String name = etEditProductName.getText().toString().trim();
        String priceStr = etEditProductPrice.getText().toString().trim();
        String quantityStr = etEditProductQuantity.getText().toString().trim();
        String description = etEditProductDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double priceValue = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            int rowsAffected = dbHelper.updateProduct(productId, name, priceValue, quantity, description);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
        }
    }
}