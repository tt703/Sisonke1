package com.example.tlotlotau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Documents.DocumentsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditCompanyInfoActivity   extends AppCompatActivity {

    private ImageButton btnBack;

    // SharedPreferences file name
    private static final String SHARED_PREFS_NAME = "CompanyInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_company_info);
        // Set up the back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });


        // Initialize UI elements
        EditText companyName = findViewById(R.id.editCompanyName);
        EditText companyAddress = findViewById(R.id.editCompanyAddress);
        EditText vatNumber = findViewById(R.id.editVATNumber);
        EditText registrationNumber = findViewById(R.id.editRegistrationNumber);
        EditText bankName = findViewById(R.id.editBankName);
        EditText accountNumber = findViewById(R.id.editAccountNumber);
        EditText branchCode = findViewById(R.id.editBranchCode);
        Button saveButton = findViewById(R.id.saveChangesButton);


        // Load saved data from SharedPreferences
        loadCompanyInfo(companyName, companyAddress, vatNumber, registrationNumber, bankName, accountNumber, branchCode);

        // Save button logic
        saveButton.setOnClickListener(v -> {
            // Capture the updated data
            String updatedCompanyName = companyName.getText().toString();
            String updatedCompanyAddress = companyAddress.getText().toString();
            String updatedVATNumber = vatNumber.getText().toString();
            String updatedRegistrationNumber = registrationNumber.getText().toString();
            String updatedBankName = bankName.getText().toString();
            String updatedAccountNumber = accountNumber.getText().toString();
            String updatedBranchCode = branchCode.getText().toString();

            // Save data to SharedPreferences
            saveCompanyInfo(updatedCompanyName, updatedCompanyAddress, updatedVATNumber,
                    updatedRegistrationNumber, updatedBankName, updatedAccountNumber, updatedBranchCode);

            // Provide user feedback
            Toast.makeText(EditCompanyInfoActivity.this, "Company information saved successfully!", Toast.LENGTH_SHORT).show();
        });




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, Settings.class));
                return true;
            } else if (itemId == R.id.nav_invoices) {
                startActivity(new Intent(this, DocumentsActivity.class));
            } else if (itemId == R.id.nav_estimates) {
                startActivity(new Intent(this, DocumentsActivity.class));
            }

            return false;

        });

        // Initia
    }

    // Method to save company information in SharedPreferences
    private void saveCompanyInfo(String companyName, String companyAddress, String vatNumber,
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
        editor.apply(); // Apply changes asynchronously
    }

    // Method to load company information from SharedPreferences
    private void loadCompanyInfo(EditText companyName, EditText companyAddress, EditText vatNumber,
                                 EditText registrationNumber, EditText bankName, EditText accountNumber,
                                 EditText branchCode) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        companyName.setText(preferences.getString("CompanyName", "BUILDERSTOWN HYPER MIDRAND")); // Default
        companyAddress.setText(preferences.getString("CompanyAddress", " "));
        vatNumber.setText(preferences.getString("VATNumber", ""));
        registrationNumber.setText(preferences.getString("RegistrationNumber", ""));
        bankName.setText(preferences.getString("BankName", ""));
        accountNumber.setText(preferences.getString("AccountNumber", ""));
        branchCode.setText(preferences.getString("BranchCode", ""));
    }
}
