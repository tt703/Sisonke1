package com.example.tlotlotau.Inventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity {

    private RecyclerView rvManageProducts;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Products");
        }

        dbHelper = new DatabaseHelper(this);
        rvManageProducts = findViewById(R.id.rvManageProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rvManageProducts.setLayoutManager(new LinearLayoutManager(this));

        loadProducts();

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ManageProductsActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    public void printQrCode(Context context, String qrCodeContent) {
        Product product = dbHelper.getProductByQRCode(qrCodeContent);

        if (product == null) {
            Toast.makeText(ManageProductsActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            File directory = new File(ManageProductsActivity.this.getExternalFilesDir(null), "qr_codes");
            if (!directory.exists())
                directory.mkdirs();

            File file = new File(directory, product.getProductName().replace(" ", "_") + "_QRCode.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            PrintHelper photoPrinter = new PrintHelper(ManageProductsActivity.this);
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            photoPrinter.printBitmap("QR Code", bitmap);
            Toast.makeText(ManageProductsActivity.this, "QR Code printed successfully", Toast.LENGTH_SHORT).show();

        } catch (WriterException | IOException e) {
            e.printStackTrace();
            Toast.makeText(ManageProductsActivity.this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProducts() {
        List<Product> products = dbHelper.getAllProducts();
        productAdapter = new ProductAdapter(this, products, new ProductAdapter.OnProductActionListener() {
            @Override
            public void onEditProduct(Product product) {
                Intent intent = new Intent(ManageProductsActivity.this, EditProductActivity.class);
                intent.putExtra("product", product.getProductId());
                startActivity(intent);
            }

            @Override
            public void onDeleteProduct(Product product) {
                int rowsAffected = dbHelper.deleteProduct(product.getProductId());
                if (rowsAffected > 0) {
                    Toast.makeText(ManageProductsActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Toast.makeText(ManageProductsActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPrintQrCode(Product product) {
                printQrCode(ManageProductsActivity.this, product.getQrCodeByHelper());
            }
        });
        rvManageProducts.setAdapter(productAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}