package com.example.tlotlotau.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;

public class AddCustomerActivity extends AppCompatActivity {

    public static final String EXTRA_CUSTOMER = "extra_customer";

    private EditText etCustomerName;
    private EditText etCustomerPhone;
    private EditText etCustomerEmail;
    private EditText etCustomerAddress;
    private Button btnSaveCustomer;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customers);

        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etCustomerEmail = findViewById(R.id.etCustomerEmail);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        btnSaveCustomer = findViewById(R.id.btnSaveCustomer);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSaveCustomer.setOnClickListener(v -> saveCustomer());
    }

    private void saveCustomer() {
        String customerName = etCustomerName.getText() == null ? "" : etCustomerName.getText().toString().trim();
        String customerPhone = etCustomerPhone.getText() == null ? "" : etCustomerPhone.getText().toString().trim();
        String customerEmail = etCustomerEmail.getText() == null ? "" : etCustomerEmail.getText().toString().trim();
        String customerAddress = etCustomerAddress.getText() == null ? "" : etCustomerAddress.getText().toString().trim();

        // validate
        if (TextUtils.isEmpty(customerName) ||
                TextUtils.isEmpty(customerPhone) ||
                TextUtils.isEmpty(customerEmail) ||
                TextUtils.isEmpty(customerAddress)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // insert into DB
        DatabaseHelper db = new DatabaseHelper(this);
        long newId = -1;
        try {
            String now = String.valueOf(System.currentTimeMillis());
            newId = db.insertCustomer(customerName, customerPhone, customerEmail, customerAddress, now);
        } finally {
            db.close();
        }

        if (newId != -1) {

            Customer newCustomer = new Customer(newId, customerName, customerPhone, customerEmail, customerAddress);
            newCustomer.setAmountDue("0.0");
            newCustomer.setNumEstimateSent("0");

            Toast.makeText(this, "Customer created", Toast.LENGTH_SHORT).show();

            // return created customer as result so calling activities/fragments can use it
            Intent result = new Intent();
            result.putExtra(EXTRA_CUSTOMER, newCustomer);
            setResult(RESULT_OK, result);

            // clear inputs (optional) and finish
            etCustomerName.setText("");
            etCustomerPhone.setText("");
            etCustomerEmail.setText("");
            etCustomerAddress.setText("");
            finish();
        } else {
            Toast.makeText(this, "Error creating customer", Toast.LENGTH_SHORT).show();
        }
    }
}
