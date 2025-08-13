package com.example.tlotlotau;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Inventory.Product;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SellProductActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvProductQuantity;
    private EditText etSellQuantity;
    private Button btnScanQRCode, btnSellProduct;
    private DatabaseHelper dbHelper;
    private Product currentProduct;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_products);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());


        dbHelper = new DatabaseHelper(this);

        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        etSellQuantity = findViewById(R.id.etSellQuantity);
        btnScanQRCode = findViewById(R.id.btnScanQRCode);
        btnSellProduct = findViewById(R.id.btnSellProduct);

        btnScanQRCode.setOnClickListener(v -> scanQRCode());
        btnSellProduct.setOnClickListener(v -> sellProduct());
    }

    private void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan the product QR Code");
        integrator.setCameraId(0); // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            String qrContent = result.getContents();
            currentProduct = dbHelper.getProductByQRCode(qrContent);
            if (currentProduct != null) {
                tvProductName.setText("Product Name: " + currentProduct.getProductName());
                tvProductPrice.setText("Price: " + currentProduct.getProductPrice());
                tvProductQuantity.setText("Quantity: " + currentProduct.getProductQuantity());
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sellProduct() {
        if (currentProduct == null) {
            Toast.makeText(this, "Please scan a product first.", Toast.LENGTH_SHORT).show();
            return;
        }
        String sellQtyStr = etSellQuantity.getText().toString().trim();
        if (sellQtyStr.isEmpty()) {
            Toast.makeText(this, "Invalid quantity.", Toast.LENGTH_SHORT).show();
            return;
        }
        int sellQty = Integer.parseInt(sellQtyStr);
        int availableQty = currentProduct.getProductQuantity();
        if (sellQty > availableQty) {
            Toast.makeText(this, "Insufficient quantity. Available: " + availableQty, Toast.LENGTH_SHORT).show();
            return;
        }
        int newQty = availableQty - sellQty;
        int rowsAffected = dbHelper.updateProductQuantity(currentProduct.getProductId(), newQty);
        if (rowsAffected > 0) {
            Toast.makeText(this, "Product sold successfully. Inventory updated.", Toast.LENGTH_SHORT).show();
            tvProductQuantity.setText("Quantity: " + newQty);
        } else {
            Toast.makeText(this, "Failed to update inventory.", Toast.LENGTH_SHORT).show();
        }
    }
}