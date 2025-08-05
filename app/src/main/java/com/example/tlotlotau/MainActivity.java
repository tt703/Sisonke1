package com.example.tlotlotau;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private Button btnSellProduct;
    private Button btnManageProducts;
    private Button btnViewDocuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, EditCompanyInfoActivity.class));
                return true;
            } else if (itemId == R.id.nav_invoices) {
                startActivity(new Intent(this, DocumentsActivity.class));
            } else if (itemId == R.id.nav_estimates) {
                startActivity(new Intent(this, DocumentsActivity.class));
            }

            return false;

        });

        // Initialize buttons (make sure these IDs exist in your activity_main.xml)
        btnViewDocuments = findViewById(R.id.btn_manage_documents);
        btnSellProduct = findViewById(R.id.btn_sell_product);
        btnManageProducts = findViewById(R.id.btn_manage_products); // Initialize if you're using this later

        // Set click listeners for each button
        btnViewDocuments.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DocumentsActivity.class)));
        btnSellProduct.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SellProductActivity.class)));
        btnManageProducts.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ManageProductsActivity.class)));
        // Set click listener for btnViewProducts if applicable

        // Initialize the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    }
}
