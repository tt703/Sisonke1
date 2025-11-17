package com.example.tlotlotau.Employees;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageEmployeesActivity extends AppCompatActivity implements EmployeeAdapter.OnItemClickListener {

    private RecyclerView rvEmployees;
    private EmployeeAdapter employeeAdapter;
    private final List<Employee> employees = new ArrayList<>();
    private final List<Employee> filtered = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration listener;
    private String businessId;
    private FloatingActionButton fabAdd;
    private ImageButton btnBack;

    // categories RV
    private RecyclerView rvRoles;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_employees);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        rvEmployees = findViewById(R.id.rvEmployees);
        fabAdd = findViewById(R.id.fabAddEmployee);
        rvRoles = findViewById(R.id.rolesfilter);

        rvEmployees.setLayoutManager(new LinearLayoutManager(this));
        employeeAdapter = new EmployeeAdapter(filtered, this);
        rvEmployees.setAdapter(employeeAdapter);

        // roles horizontal list
        rvRoles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        initCategories();
        categoryAdapter = new CategoryAdapter(categories, role -> {
            applyFilter(role);
        });
        rvRoles.setAdapter(categoryAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessId = prefs.getString("businessId", null);
        if (businessId == null || businessId.trim().isEmpty()) {
            if (auth.getCurrentUser() != null) businessId = auth.getUid();
        }

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEmployeesActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        startListening();
    }

    private void initCategories() {
        categories.clear();
        // first item = All
        categories.add(new Category("All", true));

        // read roles from string-array resources
        String[] rolesArray = getResources().getStringArray(R.array.employee_role);
        for (String r : rolesArray) {
            categories.add(new Category(r, false));
        }
    }

    private void applyFilter(String role) {
        filtered.clear();
        if (role == null || role.equalsIgnoreCase("All")) {
            filtered.addAll(employees);
        } else {
            for (Employee e : employees) {
                if (e.role != null && e.role.equalsIgnoreCase(role)) {
                    filtered.add(e);
                }
            }
        }
        employeeAdapter.notifyDataSetChanged();
    }

    private void startListening() {
        if (businessId == null || businessId.trim().isEmpty()) {
            Toast.makeText(this, "Business ID missing, cannot load employees", Toast.LENGTH_LONG).show();
            return;
        }

        listener = db.collection("businesses").document(businessId)
                .collection("employees")
                .orderBy("createdAt")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(ManageEmployeesActivity.this, "Listen error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    employees.clear();

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Employee emp = doc.toObject(Employee.class);
                            if (emp == null) emp = new Employee();

                            String uid = doc.getString("uid");
                            emp.uid = (uid != null && !uid.isEmpty()) ? uid : doc.getId();

                            // Explicit boolean read
                            Boolean isActiveBool = doc.getBoolean("isActive");
                            emp.isActive = (isActiveBool != null && isActiveBool);

                            if (emp.name == null) emp.name = doc.getString("name");
                            if (emp.email == null) emp.email = doc.getString("email");
                            if (emp.phone == null) emp.phone = doc.getString("phone");
                            if (emp.role == null) emp.role = doc.getString("role");

                            employees.add(emp);
                        }
                    }

                    // re-apply current selected filter
                    String selectedRole = "All";
                    for (Category c : categories) if (c.isSelected) { selectedRole = c.name; break; }

                    applyFilter(selectedRole);
                });
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
