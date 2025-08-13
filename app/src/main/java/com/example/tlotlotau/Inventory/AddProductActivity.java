package com.example.tlotlotau.Inventory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.DatabaseHelper;
import com.example.tlotlotau.R;

public class AddProductActivity extends AppCompatActivity {
    private EditText etProductName, etProductPrice, etProductQuantity, etProductDescription;
    private ImageView ivQRCode;
    private Button btnGenerateQRCode, btnSaveProduct;

    private DatabaseHelper dbHelper;
    private Bitmap generatedQRBitmap;
    private String generatedQRContent;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setClickListeners();
        btnBack = findViewById(R.id.btnBack);

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
    }

    private void setClickListeners() {
        btnGenerateQRCode.setOnClickListener(v -> generateQRCode());
        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private boolean validateInput() {
        if (etProductName.getText().toString().trim().isEmpty() ||
                etProductPrice.getText().toString().trim().isEmpty() ||
                etProductQuantity.getText().toString().trim().isEmpty() ||
                etProductDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void generateQRCode() {
        if (!validateInput()) return;

        generatedQRContent = etProductName.getText().toString().trim() + "_" + System.currentTimeMillis();
        generatedQRBitmap = QRCodeHelper.generateQRCode(generatedQRContent, 300, 300);

        if (generatedQRBitmap != null) {
            ivQRCode.setImageBitmap(generatedQRBitmap);
        } else {
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
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

            // Save the product in the database.
            long result = dbHelper.insertProduct(name, price, quantity, generatedQRContent);

            if (result != -1) {
                Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                // Redirect to ManageProductsActivity after a successful save.
                Intent intent = new Intent(AddProductActivity.this, ManageProductsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
        }
    }
}
