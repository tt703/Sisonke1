package com.example.tlotlotau.Customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

    private EditText etName, etEmail, etPhone, etAddress;
    private TextView tvInvoicesMade, tvEstimatesMade, tvAmountDue; // stats area
    private Button btnSave, btnDelete;
    private ImageButton btnBack;

    private Customer customer;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etCustomerName);
        etEmail = findViewById(R.id.etCustomerEmail);
        etPhone = findViewById(R.id.etCustomerPhone);
        etAddress = findViewById(R.id.etCustomerAddress);
        tvInvoicesMade = findViewById(R.id.tvInvoicesMade);
        tvEstimatesMade = findViewById(R.id.tvEstimatesMade);
        tvAmountDue = findViewById(R.id.tvAmountDue);
        btnSave = findViewById(R.id.btnSaveCustomer);
        btnDelete = findViewById(R.id.btnDeleteCustomer);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // read parcelable customer passed from ManageCustomerActivity
        customer = getIntent().getParcelableExtra("customer");
        if (customer == null) {
            Toast.makeText(this, "Customer not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateFields();   // fill editable fields (name, email, phone, address)
        loadStats();        // load amount due, invoices & estimates counts from DB (overwrites tvAmountDue)

        btnSave.setOnClickListener(v -> saveCustomer());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void populateFields() {
        etName.setText(customer.getName());
        etEmail.setText(customer.getEmail());
        etPhone.setText(customer.getPhone());
        etAddress.setText(customer.getAddress());
        // show placeholder while we fetch fresh amount
        tvAmountDue.setText("R 0.00");
    }

    private void loadStats() {
        // Use customer name as the key (simple approach)
        String name = customer.getName() == null ? "" : customer.getName();

        // invoice/estimate counts
        int invoices = db.getInvoiceCountForCustomer(name);
        int estimates = db.getEstimateCountForCustomer(name);

        // sum of invoice totals (amount due)
        double amountDue = db.getInvoiceAmountForCustomer(name);

        // format amount to 2 decimal places and show with R prefix
        String formattedAmount = String.format(Locale.getDefault(), "R %.2f", amountDue);

        tvInvoicesMade.setText(String.valueOf(invoices));
        tvEstimatesMade.setText(String.valueOf(estimates));
        tvAmountDue.setText(formattedAmount);

        // also update the Customer object so other screens/readers get the fresh value
        customer.setAmountDue(String.format(Locale.getDefault(), "%.2f", amountDue));
        customer.setNumEstimateSent(String.valueOf(estimates));
    }

    private void saveCustomer() {
        String name = etName.getText() == null ? "" : etName.getText().toString().trim();
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String phone = etPhone.getText() == null ? "" : etPhone.getText().toString().trim();
        String address = etAddress.getText() == null ? "" : etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = customer.getId();
        Log.d("EditCustomer", "Attempting update for local id=" + id + " name=" + name);

        // quick existence check (helps debug wrong id)
        Cursor check = db.getReadableDatabase().query(DatabaseHelper.TABLE_CUSTOMERS, null,
                DatabaseHelper.COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        boolean exists = (check != null && check.moveToFirst());
        Log.d("EditCustomer", "Row exists before update? " + exists);
        if (check != null) check.close();

        int rows = db.updateCustomer(id, name, phone, email, address);
        Log.d("EditCustomer", "update returned rows=" + rows);

        if (rows > 0) {
            Toast.makeText(this, "Customer updated", Toast.LENGTH_SHORT).show();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);
            Intent result = new Intent();
            result.putExtra(EXTRA_CUSTOMER_UPDATED, customer);
            setResult(RESULT_OK, result);
            finish();
        } else {
            Toast.makeText(this, "Failed to update customer (no rows affected)", Toast.LENGTH_SHORT).show();
        }
    }


    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete customer")
                .setMessage("Are you sure you want to delete \"" + customer.getName() + "\"? This will not remove associated invoices/estimates.")
                .setPositiveButton("Delete", (d, w) -> {
                    int rows = db.deleteCustomer(customer.getId());
                    if (rows > 0) {
                        Toast.makeText(this, "Customer deleted", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // caller can reload
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
        db.close();
    }
}
