package com.example.tlotlotau.Customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;

import java.util.Locale;

public class EditCustomerActivity extends AppCompatActivity {

    public static final String EXTRA_CUSTOMER_UPDATED = "extra_customer_updated";

    // UI References
    private EditText etName, etEmail, etPhone, etAddress;
    private TextView tvInvoicesMade, tvEstimatesMade, tvAmountDue;
    private Button btnSave, btnDelete;
    private ImageButton btnBack;

    private Customer customer;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        db = new DatabaseHelper(this);
        initViews();

        // 1. Retrieve Customer Data
        customer = getIntent().getParcelableExtra("customer");

        // 2. Safety Checks
        if (customer == null) {
            Toast.makeText(this, "Error: Customer data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Check ID Validity (Fixes "Cannot Delete/Edit" bug)
        if (customer.getId() == 0) {
            Toast.makeText(this, "Invalid Customer ID. Editing disabled.", Toast.LENGTH_LONG).show();
            btnSave.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        // 4. Load Data
        populateFields();
        loadStats();

        // 5. Set Listeners
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveCustomer());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void initViews() {
        etName = findViewById(R.id.etCustomerName);
        etEmail = findViewById(R.id.etCustomerEmail);
        etPhone = findViewById(R.id.etCustomerPhone);
        etAddress = findViewById(R.id.etCustomerAddress);

        // Stats TextViews
        tvInvoicesMade = findViewById(R.id.tvInvoicesMade);
        tvEstimatesMade = findViewById(R.id.tvEstimatesMade);
        tvAmountDue = findViewById(R.id.tvAmountDue);

        // Buttons
        btnSave = findViewById(R.id.btnSaveCustomer);
        btnDelete = findViewById(R.id.btnDeleteCustomer);
        btnBack = findViewById(R.id.btnBack);
    }

    private void populateFields() {
        etName.setText(customer.getName());
        etEmail.setText(customer.getEmail());
        etPhone.setText(customer.getPhone());
        etAddress.setText(customer.getAddress());
    }

    private void loadStats() {
        // Stats are loaded fresh from the DB to ensure accuracy
        String name = customer.getName() == null ? "" : customer.getName();

        int invoices = db.getInvoiceCountForCustomer(name);
        int estimates = db.getEstimateCountForCustomer(name);
        double amountDue = db.getInvoiceAmountForCustomer(name);

        tvInvoicesMade.setText(String.valueOf(invoices));
        tvEstimatesMade.setText(String.valueOf(estimates));
        tvAmountDue.setText(String.format(Locale.getDefault(), "R %.2f", amountDue));
    }

    private void saveCustomer() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone is required");
            return;
        }

        // Update in DB
        int rows = db.updateCustomer(customer.getId(), name, phone, email, address);

        if (rows > 0) {
            Toast.makeText(this, "Customer updated", Toast.LENGTH_SHORT).show();

            // Update the local object to pass back to the list
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);

            Intent result = new Intent();
            result.putExtra(EXTRA_CUSTOMER_UPDATED, customer);
            setResult(RESULT_OK, result);
            finish();
        } else {
            Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete " + customer.getName() + "?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rows = db.deleteCustomer(customer.getId());
                    if (rows > 0) {
                        Toast.makeText(this, "Customer deleted", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Notify list to refresh
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete customer", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}