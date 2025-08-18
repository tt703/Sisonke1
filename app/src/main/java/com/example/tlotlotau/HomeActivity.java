package com.example.tlotlotau;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;

import com.example.tlotlotau.Auth.ProfileActivity;
import com.example.tlotlotau.Documents.DocumentsActivity;
import com.example.tlotlotau.Inventory.ManageProductsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.example.tlotlotau.Documents.DocumentsActivity;
import com.example.tlotlotau.Inventory.ManageProductsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.tlotlotau.Auth.ProfileActivity;


public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView tv_main_title;
    ImageButton btn_profile;
    private Button btnSellProduct;
    private Button btnManageProducts;
    private Button btnViewDocuments;
    private Button btnProfile;



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
        btnViewDocuments = findViewById(R.id.btn_manage_documents);
        btnSellProduct = findViewById(R.id.btn_sell_product);
        btnManageProducts = findViewById(R.id.btn_manage_products);


        mAuth = FirebaseAuth.getInstance();
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("Welcome " + mAuth.getCurrentUser().getEmail().toString());
        btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(View -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        btnViewDocuments.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, DocumentsActivity.class)));
        btnSellProduct.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SellProductActivity.class)));
        btnManageProducts.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ManageProductsActivity.class)));

        btnProfile.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null)
            {
                Toast.makeText(getApplicationContext(), "No User Found",Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
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
    }
}