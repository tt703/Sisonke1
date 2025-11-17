package com.example.tlotlotau.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;


import com.example.tlotlotau.Customer.AddCustomerActivity;
import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Customer.CustomerAdapter;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManageCustomerActivity extends  AppCompatActivity {
    private RecyclerView rvCustomers;
    private CustomerAdapter customerAdapter;

    private final List<Customer> customers = new ArrayList<>();

    private FloatingActionButton fabAdd;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_customers);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        rvCustomers = findViewById(R.id.rvCustomers);
        fabAdd = findViewById(R.id.fabAddCustomer);

        customerAdapter = new CustomerAdapter(customers,null);
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        rvCustomers.setAdapter(customerAdapter);


        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCustomerActivity.this, AddCustomerActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected  void onResume(){
        super.onResume();
        loadCustomers();
    }

    private void loadCustomers(){
        DatabaseHelper db = new DatabaseHelper(this);
        List<Customer> Customers = db.getAllCustomers();
        customers.clear();
        customers.addAll(db.getAllCustomers());
        customerAdapter.notifyDataSetChanged();
    }




}
