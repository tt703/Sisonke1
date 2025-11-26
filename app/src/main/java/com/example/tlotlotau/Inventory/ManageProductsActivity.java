package com.example.tlotlotau.Inventory;

import android.app.AlertDialog; // Import added
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
import com.example.tlotlotau.Main.HomeActivity;
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
import java.util.ArrayList;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity {

    private RecyclerView rvManageProducts, rvCategories;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private DatabaseHelper dbHelper;
    private ImageButton btnBack;
    private ImageButton btnManageCategories;
    private CategoryAdapter2 categoryAdapter2;
    private long currentCategoryFilter = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        dbHelper = new DatabaseHelper(this);

        rvCategories = findViewById(R.id.rvCategories);
        btnManageCategories = findViewById(R.id.btnManageCategories);
        rvManageProducts = findViewById(R.id.rvManageProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v-> startActivity(new Intent(ManageProductsActivity.this, HomeActivity.class)));

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setHasFixedSize(true);
        rvManageProducts.setLayoutManager(new LinearLayoutManager(this));
        rvManageProducts.setHasFixedSize(true);

        categoryAdapter2 = new CategoryAdapter2(new ArrayList<>(), c -> {
            currentCategoryFilter = (c == null) ? -1L : c.getId();
            categoryAdapter2.setSelectedCategoryId(currentCategoryFilter);
            loadProducts();
        });
        rvCategories.setAdapter(categoryAdapter2);

        loadCategories();
        loadProducts();

        btnManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(ManageProductsActivity.this, CategoryManagerActivity.class);
            startActivity(intent);
        });

        fabAddProduct.setOnClickListener(v -> {
            List<CategoryC> cats = dbHelper.getAllCategories();
            if (cats == null || cats.isEmpty()) {
                Toast.makeText(this, "Please add a category first", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(ManageProductsActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadCategories() {
        if (categoryAdapter2 == null) {
            categoryAdapter2 = new CategoryAdapter2(new ArrayList<>(), c -> {
                currentCategoryFilter = (c == null) ? -1L : c.getId();
                if (categoryAdapter2 != null) categoryAdapter2.setSelectedCategoryId(currentCategoryFilter);
                loadProducts();
            });
            rvCategories.setAdapter(categoryAdapter2);
        }

        List<CategoryC> categories = dbHelper.getAllCategories();
        CategoryC allCategory = new CategoryC(-1L, "All", String.valueOf(System.currentTimeMillis()));

        List<CategoryC> displayList = new ArrayList<>();
        displayList.add(allCategory);
        if (categories != null) displayList.addAll(categories);

        categoryAdapter2.updateData(displayList);
        categoryAdapter2.setSelectedCategoryId(currentCategoryFilter);
    }

    private void loadProducts() {
        List<Product> products;
        if (currentCategoryFilter == -1L) {
            products = dbHelper.getAllProducts();
        } else {
            products = dbHelper.getProductsByCategory(currentCategoryFilter);
        }

        productAdapter = new ProductAdapter(this, products, new ProductAdapter.OnProductActionListener() {
            @Override
            public void onEditProduct(Product product) {
                Intent intent = new Intent(ManageProductsActivity.this, EditProductActivity.class);
                intent.putExtra("product", product.getProductId());
                startActivity(intent);
            }

            @Override
            public void onDeleteProduct(Product product) {
                // FIX: Added Confirmation Dialog
                new AlertDialog.Builder(ManageProductsActivity.this)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete " + product.getProductName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int rowsAffected = dbHelper.deleteProduct(product.getProductId());
                            if (rowsAffected > 0) {
                                Toast.makeText(ManageProductsActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                loadProducts(); // Refresh list
                            } else {
                                Toast.makeText(ManageProductsActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

            @Override
            public void onPrintQrCode(Product product) {
                printQrCode(ManageProductsActivity.this, product.getQrCodeByHelper());
            }
        });

        rvManageProducts.setAdapter(productAdapter);
    }

    // ... (keep existing printQrCode, onResume, etc.)
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
            if (!directory.exists()) directory.mkdirs();

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

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
        loadProducts();
    }
}