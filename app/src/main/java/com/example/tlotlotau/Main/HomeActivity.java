package com.example.tlotlotau.Main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlotlotau.Auth.ProfileActivity;
import com.example.tlotlotau.Documents.DocumentsActivity;
import com.example.tlotlotau.Inventory.ManageProductsActivity;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Sales.SellProductActivity;
import com.example.tlotlotau.Settings.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView Welcome;
    ImageButton btn_profile;
    private Button btnSellProduct;
    private Button btnManageProducts;
    private Button btnViewDocuments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        btnViewDocuments = findViewById(R.id.btn_manage_documents);
        btnSellProduct = findViewById(R.id.btn_sell_product);
        btnManageProducts = findViewById(R.id.btn_manage_products);
        btn_profile = findViewById(R.id.btn_profile); // This is an ImageButton

        Welcome = findViewById(R.id.Welcome);
        mAuth = FirebaseAuth.getInstance();

        // Set welcome message (with null checks)
        // Consider using string resources for "Welcome " and "No User Found" for localization.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            Welcome.setText("Welcome " + currentUser.getDisplayName());
        } else {
            Welcome.setText("Welcome");
        }

        // Set click listeners
        btn_profile.setOnClickListener(view -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }

        });

        btnViewDocuments.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, DocumentsActivity.class)));
        btnSellProduct.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, SellProductActivity.class)));
        btnManageProducts.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ManageProductsActivity.class)));

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already in HomeActivity, might not need to restart it unless specific state reset is needed
                // startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, Settings.class));
                return true;
            } else if (itemId == R.id.nav_invoices) {
                startActivity(new Intent(this, DocumentsActivity.class));
                return true; // Added missing return true
            } else if (itemId == R.id.nav_estimates) {
                startActivity(new Intent(this, DocumentsActivity.class));
                return true; // Added missing return true
            }
            return false;
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();

            }

        });
        //check shared preference is not empty if user is Owner




    }
}
