package com.example.tlotlotau.Employees;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.MainActivity;
import com.example.tlotlotau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageEmployeesActivity extends AppCompatActivity implements EmployeeAdapter.OnItemClickListener {

    private RecyclerView rv;
    private EmployeeAdapter adapter;
    private List<Employee> list = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration listener;
    private String businessId;
    private FloatingActionButton fabAdd;
    private ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure this matches your layout filename in res/layout (use activity_manage_employees.xml)
        setContentView(R.layout.manage_employees);
        btn = findViewById(R.id.btnBack);
        btn.setOnClickListener(v -> onBackPressed());

        //rv2= findViewById(R.id.rolesfilter);
        //rv2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
       // rv2.setAdapter(new RolesFilterAdapter(this));



        rv = findViewById(R.id.rvEmployees);
        fabAdd = findViewById(R.id.fabAddEmployee);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmployeeAdapter(list, this);
        rv.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Use consistent SharedPreferences keys used elsewhere in your app
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessId = prefs.getString("businessId", auth.getUid());
        if (businessId == null) businessId = auth.getUid();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEmployeesActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        startListening();
    }

    private void startListening() {
        // Listen for employees under this business (real-time)
        listener = db.collection("businesses").document(businessId)
                .collection("employees")
                .orderBy("createdAt")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(ManageEmployeesActivity.this, "Listen error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    list.clear();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Employee emp = doc.toObject(Employee.class);
                            list.add(emp);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No-op: the real-time listener keeps data current.
        // You can optionally re-query here if you prefer
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }

    @Override
    public void onItemClick(Employee employee) {
        Intent intent = new Intent(this, EditEmployeeActivity.class);
        intent.putExtra("employeeUid", employee.uid);
        startActivity(intent);
    }
}
