package com.example.tlotlotau.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Documents.DocumentsActivity;
import com.example.tlotlotau.Main.HomeActivity;
import com.example.tlotlotau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManageCustomerActivity extends AppCompatActivity {
    private RecyclerView rvCustomers;
    private CustomerAdapter customerAdapter;

    // backing list used by adapter
    private final List<Customer> customers = new ArrayList<>();

    private FloatingActionButton fabAdd;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_customers);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> startActivity(new Intent(ManageCustomerActivity.this, HomeActivity.class)));

        rvCustomers = findViewById(R.id.rvCustomers);
        fabAdd = findViewById(R.id.fabAddCustomer);

        // Create adapter and wire click -> EditCustomerActivity
        customerAdapter = new CustomerAdapter(customers, customer -> {
            Intent i = new Intent(ManageCustomerActivity.this, EditCustomerActivity.class);
            i.putExtra("customer", customer); // parcelable
            startActivity(i);
        });

        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        rvCustomers.setAdapter(customerAdapter);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCustomerActivity.this, AddCustomerActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomers();
    }

    /**
     * Loads customers from the DB and enriches each customer with:
     *  - amountDue (sum of invoice totals for that customer)
     *  - numEstimatesSent (count of estimates for that customer)
     *
     * Uses DatabaseHelper helper methods added below.
     */
    private void loadCustomers() {
        DatabaseHelper db = new DatabaseHelper(this);
        try {
            List<Customer> dbCustomers = db.getAllCustomers();
            if (dbCustomers == null) dbCustomers = new ArrayList<>();

            // enrich each customer with stats
            for (Customer c : dbCustomers) {
                String name = c.getName() == null ? "" : c.getName();

                // invoice total (amount due) and estimate count
                double amountDue = db.getInvoiceAmountForCustomer(name);
                int estimateCount = db.getEstimateCountForCustomer(name);

                // store back into model (Customer class in your project has setters used before)
                c.setAmountDue(String.format("%.2f", amountDue));
                c.setNumEstimateSent(String.valueOf(estimateCount));
            }

            // update adapter
            customers.clear();
            customers.addAll(dbCustomers);
            if (customerAdapter != null) customerAdapter.updateData(customers);
            else rvCustomers.setAdapter(new CustomerAdapter(customers, customer -> {
                Intent i = new Intent(ManageCustomerActivity.this, EditCustomerActivity.class);
                i.putExtra("customer", customer);
                startActivity(i);
            }));
        } finally {
            db.close();
        }
    }
}
