package com.example.tlotlotau;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Auth.LoginActivity;
import com.example.tlotlotau.Documents.DocumentsActivity;
import com.example.tlotlotau.Inventory.ManageProductsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.tlotlotau.Auth.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.example.tlotlotau.Auth.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSellProduct;
    private Button btnManageProducts;
    private Button btnViewDocuments;

    private Button btnRegister;
    private Button btnProfile;
    private Button btnLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_LONG).show();
        }



        //Bottom Navigation Logic
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
        btnManageProducts = findViewById(R.id.btn_manage_products);
        btnRegister = findViewById(R.id.btnRegister);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogin = findViewById(R.id.btnLogin);


        // Set click listeners for each button
        btnViewDocuments.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DocumentsActivity.class)));
        btnSellProduct.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SellProductActivity.class)));
        btnManageProducts.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ManageProductsActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        // Profile Button checks if user exists first
        btnProfile.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null)
                {
                    Toast.makeText(getApplicationContext(), "No User Found",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
        });
        // Initialize the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    }
}
