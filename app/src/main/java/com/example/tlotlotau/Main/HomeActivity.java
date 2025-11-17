package com.example.tlotlotau.Main;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlotlotau.Auth.ProfileActivity;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Reports.ReportsActivity;
import com.example.tlotlotau.Settings.EditCompanyInfoActivity;
import com.example.tlotlotau.Settings.Settings;
import com.example.tlotlotau.Documents.DocumentsActivity;
import com.example.tlotlotau.Inventory.ManageProductsActivity;
import com.example.tlotlotau.Sales.SellProductActivity;
import com.example.tlotlotau.Customer.ManageCustomerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView Welcome;
    private ImageButton btn_profile;
    private Button btnSellProduct;
    private Button btnManageProducts;
    private Button btnViewDocuments;
    private Button btnManageCustomers;
    private Button btn_manage_reports;


    private AlertDialog loadingDialog;

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

        initViews();
        initFirebase();
        initLoadingDialog();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Not signed in -> go to login
            startActivity(new Intent(HomeActivity.this, com.example.tlotlotau.Auth.LoginActivity.class));
            finish();
            return;
        }

        showLoading(true);

        currentUser.reload().addOnCompleteListener(task -> {
            FirebaseUser reloaded = mAuth.getCurrentUser();
            String welcomeName = determineDisplayName(reloaded);
            Welcome.setText("Welcome " + welcomeName);

            checkUserAndEmployeeFlagsThenProceed(reloaded.getUid());
        }).addOnFailureListener(e -> {
            // If reload fails, hide loader and show an error then redirect to login for safety
            showLoading(false);
            mAuth.signOut();
            Toast.makeText(HomeActivity.this, "Failed to refresh user: " + e.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(HomeActivity.this, com.example.tlotlotau.Auth.LoginActivity.class));
            finish();
        });

        // click handlers
        btn_profile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        btnViewDocuments.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, DocumentsActivity.class)));
        btnSellProduct.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SellProductActivity.class)));
        btnManageProducts.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ManageProductsActivity.class)));
        btnManageCustomers.setOnClickListener(v-> startActivity(new Intent(HomeActivity.this,ManageCustomerActivity.class)));
        btn_manage_reports.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ReportsActivity.class)));



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            if (itemId == R.id.nav_settings) { startActivity(new Intent(this, Settings.class)); return true; }
            if (itemId == R.id.nav_invoices) { startActivity(new Intent(this, DocumentsActivity.class)); return true; }
            if (itemId == R.id.nav_estimates) { startActivity(new Intent(this, DocumentsActivity.class)); return true; }
            return false;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finishAffinity(); }
        });
    }

    private void initViews() {
        Welcome = findViewById(R.id.Welcome);
        btn_profile = findViewById(R.id.btn_profile);
        btnViewDocuments = findViewById(R.id.btn_manage_documents);
        btnSellProduct = findViewById(R.id.btn_sell_product);
        btnManageProducts = findViewById(R.id.btn_manage_products);
        btnManageCustomers = findViewById(R.id.btnManageCustomers);
        btn_manage_reports = findViewById(R.id.btn_manage_reports);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initLoadingDialog() {
        ProgressBar pb = new ProgressBar(this);
        pb.setIndeterminate(true);
        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setView(pb)
                .setCancelable(false);
        loadingDialog = b.create();
    }

    private void showLoading(boolean show) {
        if (loadingDialog == null) return;
        if (show) {
            if (!loadingDialog.isShowing()) loadingDialog.show();
        } else {
            if (loadingDialog.isShowing()) loadingDialog.dismiss();
        }
    }


    private String determineDisplayName(com.google.firebase.auth.FirebaseUser user) {
        if (user == null) return "User";
        String name = user.getDisplayName();
        if (name != null && !name.trim().isEmpty()) return name;

        // try fallback to email
        String email = user.getEmail();
        if (email != null && !email.trim().isEmpty()) return email;

        return "User";
    }

    private void checkUserAndEmployeeFlagsThenProceed(String uid) {
        if (uid == null) {
            showLoading(false);
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            startActivity(new Intent(this, com.example.tlotlotau.Auth.LoginActivity.class));
            finish();
            return;
        }

        db.runTransaction((com.google.firebase.firestore.Transaction.Function<Map<String, Object>>) transaction -> {
            // read users/{uid}
            DocumentReference userRef = db.collection("users").document(uid);
            com.google.firebase.firestore.DocumentSnapshot userSnap = transaction.get(userRef);
            if (!userSnap.exists()) {
                throw new RuntimeException("User record not found");
            }

            String role = userSnap.getString("role");
            String businessId = userSnap.getString("businessId");
            if (businessId == null || businessId.trim().isEmpty()) businessId = uid;

            // read businesses/{businessId}/employees/{uid} (may not exist for non-employees)
            DocumentReference empRef = db.collection("businesses")
                    .document(businessId)
                    .collection("employees")
                    .document(uid);
            com.google.firebase.firestore.DocumentSnapshot empSnap = transaction.get(empRef);

            Boolean isActive = null;
            Boolean mustChange = null;
            if (empSnap.exists()) {
                isActive = empSnap.getBoolean("isActive");
                mustChange = empSnap.getBoolean("mustChangePassword");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("role", role);
            result.put("businessId", businessId);
            result.put("isActive", isActive);
            result.put("mustChange", mustChange);
            return result;
        }).addOnSuccessListener(result -> {
            // Hide loader once transaction completes
            showLoading(false);

            // Persist role and businessId to shared prefs
            String role = (String) result.get("role");
            String businessId = (String) result.get("businessId");
            Boolean isActive = (Boolean) result.get("isActive");
            Boolean mustChange = (Boolean) result.get("mustChange");

            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if (role != null) editor.putString("user_role", role);
            if (businessId != null) editor.putString("businessId", businessId);
            editor.apply();

            // Block if explicitly deactivated
            if (Boolean.FALSE.equals(isActive)) {
                mAuth.signOut();
                Toast.makeText(HomeActivity.this, "Your account is deactivated. Contact owner.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(HomeActivity.this, com.example.tlotlotau.Auth.LoginActivity.class));
                finish();
                return;
            }

            // Force password change if required (any user with mustChange true)
            if (Boolean.TRUE.equals(mustChange)) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                i.putExtra("forceChange", true);
                // prevent going back to Home before change completed
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                return;
            }

            // If owner, ensure company info exists in CompanyInfo prefs
            if ("owner".equalsIgnoreCase(role)) {
                SharedPreferences sp = getSharedPreferences("CompanyInfo", MODE_PRIVATE);
                String companyName = sp.getString("CompanyName", null);
                if (companyName == null || companyName.trim().isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Please complete your company profile", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(HomeActivity.this, EditCompanyInfoActivity.class));
                    finish();
                    return;
                }
            }

            // All checks pass — remain in HomeActivity
        }).addOnFailureListener(exc -> {
            // Hide loader and sign out for safety
            showLoading(false);
            mAuth.signOut();
            Toast.makeText(HomeActivity.this, "Failed to verify account: " + exc.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(HomeActivity.this, com.example.tlotlotau.Auth.LoginActivity.class));
            finish();
        });
    }
}
