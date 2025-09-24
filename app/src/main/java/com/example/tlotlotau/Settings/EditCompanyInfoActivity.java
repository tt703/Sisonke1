package com.example.tlotlotau.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.DatabaseHelper;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Auth.LoginActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * EditCompanyInfoActivity
 * - Loads company info from Firestore (if present) into the UI
 * - Saves to Firestore, SharedPreferences and local SQLite (DatabaseHelper)
 * - Only accessible by owner role (users/{uid}.role must be "owner")
 */
public class EditCompanyInfoActivity extends AppCompatActivity {

    private ImageButton btnBack;

    // SharedPreferences file name
    private static final String SHARED_PREFS_NAME = "CompanyInfo";

    // UI
    private EditText companyNameEt, companyAddressEt, vatNumberEt, registrationNumberEt,
            bankNameEt, accountNumberEt, branchCodeEt;
    private Button saveButton;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;
    private String businessId;

    // Local DB
    private DatabaseHelper localDb;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_company_info);

        // Views
        btnBack = findViewById(R.id.btnBack);
        companyNameEt = findViewById(R.id.editCompanyName);
        companyAddressEt = findViewById(R.id.editCompanyAddress);
        vatNumberEt = findViewById(R.id.editVATNumber);
        registrationNumberEt = findViewById(R.id.editRegistrationNumber);
        bankNameEt = findViewById(R.id.editBankName);
        accountNumberEt = findViewById(R.id.editAccountNumber);
        branchCodeEt = findViewById(R.id.editBranchCode);
        saveButton = findViewById(R.id.saveChangesButton);

        btnBack.setOnClickListener(v -> onBackPressed());

        // Init Firebase + local DB + prefs
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        localDb = new DatabaseHelper(getApplicationContext());
        prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        // Ensure user logged in
        FirebaseUser current = mAuth.getCurrentUser();
        if (current == null) {
            Toast.makeText(this, "Please sign in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = current.getUid();

        // Check role from users/{uid} and then load business details
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(this::onUserDocLoaded)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to check user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
        loadCompanyInfoFromPrefs();

        // Save action
        saveButton.setOnClickListener(v -> saveCompanyInfo());
    }

    private void onUserDocLoaded(DocumentSnapshot userDoc) {
        if (!userDoc.exists()) {
            Toast.makeText(this, "User record not found. Please login again.", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        String role = userDoc.getString("role");
        if (role == null || !role.equals("owner")) {
            Toast.makeText(this, "Access denied. Only the owner can edit company info.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // businessId stored in users/{uid}.businessId (fallback to uid)
        businessId = userDoc.getString("businessId");
        if (TextUtils.isEmpty(businessId)) businessId = uid;

        // Save businessId locally for quick access
        prefs.edit().putString("businessId", businessId).apply();

        // Load business document from Firestore and overwrites Prefs if present
        db.collection("businesses").document(businessId)
                .get()
                .addOnSuccessListener(this::onBusinessDocLoaded)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load business: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void onBusinessDocLoaded(DocumentSnapshot businessDoc) {
        if (businessDoc.exists()) {
            // populate UI fields with data from Firestore
            companyNameEt.setText(nullToEmpty(businessDoc.getString("name")));
            companyAddressEt.setText(nullToEmpty(businessDoc.getString("address")));
            vatNumberEt.setText(nullToEmpty(businessDoc.getString("vatNumber")));
            registrationNumberEt.setText(nullToEmpty(businessDoc.getString("registrationNumber")));
            bankNameEt.setText(nullToEmpty(businessDoc.getString("bankName")));
            accountNumberEt.setText(nullToEmpty(businessDoc.getString("accountNumber")));
            branchCodeEt.setText(nullToEmpty(businessDoc.getString("branchCode")));
        }
    }

    private void saveCompanyInfo() {
        String name = companyNameEt.getText().toString().trim();
        String address = companyAddressEt.getText().toString().trim();
        String vat = vatNumberEt.getText().toString().trim();
        String regNo = registrationNumberEt.getText().toString().trim();
        String bankName = bankNameEt.getText().toString().trim();
        String accountNo = accountNumberEt.getText().toString().trim();
        String branchCode = branchCodeEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            companyNameEt.setError("Company name required");
            companyNameEt.requestFocus();
            return;
        }

        // Prepare map for Firestore
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("ownerId", uid);
        businessData.put("name", name);
        businessData.put("address", address);
        businessData.put("vatNumber", vat);
        businessData.put("registrationNumber", regNo);
        businessData.put("bankName", bankName);
        businessData.put("accountNumber", accountNo);
        businessData.put("branchCode", branchCode);
        businessData.put("updatedAt", Timestamp.now());

        if (TextUtils.isEmpty(businessId)) businessId = uid;

        // Write to Firestore
        db.collection("businesses").document(businessId)
                .set(businessData)
                .addOnSuccessListener(aVoid -> {
                    // 1) Save to SharedPreferences (keeps your existing flows working)
                    saveCompanyInfoToPrefs(name, address, vat, regNo, bankName, accountNo, branchCode);

                    // 2) Upsert local DB row (synced = 1)
                    long now = System.currentTimeMillis();
                    boolean ok = localDb.upsertBusinessLocal(
                            businessId, name, address, vat, regNo,
                            bankName, accountNo, branchCode, uid, now, 1
                    );
                    if (!ok) {
                        Toast.makeText(this, "Saved to Firestore but failed to save locally", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 3) Ensure users/{uid}.businessId points to this businessId
                    Map<String, Object> userUpdate = new HashMap<>();
                    userUpdate.put("businessId", businessId);
                    db.collection("users").document(uid).update(userUpdate);

                    Toast.makeText(this, "Company information saved", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save company: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveCompanyInfoToPrefs(String companyName, String companyAddress, String vatNumber,
                                        String registrationNumber, String bankName, String accountNumber,
                                        String branchCode) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CompanyName", companyName);
        editor.putString("CompanyAddress", companyAddress);
        editor.putString("VATNumber", vatNumber);
        editor.putString("RegistrationNumber", registrationNumber);
        editor.putString("BankName", bankName);
        editor.putString("AccountNumber", accountNumber);
        editor.putString("BranchCode", branchCode);
        editor.apply();
    }

    private void loadCompanyInfoFromPrefs() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        companyNameEt.setText(preferences.getString("CompanyName", ""));
        companyAddressEt.setText(preferences.getString("CompanyAddress", ""));
        vatNumberEt.setText(preferences.getString("VATNumber", ""));
        registrationNumberEt.setText(preferences.getString("RegistrationNumber", ""));
        bankNameEt.setText(preferences.getString("BankName", ""));
        accountNumberEt.setText(preferences.getString("AccountNumber", ""));
        branchCodeEt.setText(preferences.getString("BranchCode", ""));
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
