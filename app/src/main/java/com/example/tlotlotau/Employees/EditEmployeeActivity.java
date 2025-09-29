package com.example.tlotlotau.Employees;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tlotlotau.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditEmployeeActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone;
    private Spinner spinnerRole;
    private ImageButton btnBack;
    private CheckBox cbIsActive;
    private Button btnSave;
    private TextView tvStatus;

    private String employeeUid;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String businessId;

    private boolean originalIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        initUI();
        initFirebase();
        setupListeners();

        employeeUid = getIntent().getStringExtra("employeeUid");
        if (TextUtils.isEmpty(employeeUid)) {
            Toast.makeText(this, "Missing employee ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //prefer cached value, fallback to auth uid
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessId = prefs.getString("business_id", null);
        if (businessId == null || businessId.trim().isEmpty()) {
            if (mAuth.getCurrentUser() != null) businessId = mAuth.getUid();
        }

        loadEmployee();
    }

    private void initUI() {
        etName = findViewById(R.id.etEmpName);
        etEmail = findViewById(R.id.etEmpEmail);
        etPhone = findViewById(R.id.etEmpPhone);
        spinnerRole = findViewById(R.id.spinnerRole);
        cbIsActive = findViewById(R.id.cbEmpActive);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        tvStatus = findViewById(R.id.etStatus);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Live-update status text when checkbox toggles
        cbIsActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvStatus.setText(isChecked ? "Active" : "Disabled");
            tvStatus.setTextColor(
                    isChecked ? ContextCompat.getColor(this, R.color.green)
                            : ContextCompat.getColor(this, R.color.red)
            );
        });

        btnSave.setOnClickListener(v -> {
            boolean willBeActive = cbIsActive.isChecked();
            if (originalIsActive && !willBeActive) {
                showDeactivationDialog();
            } else {
                saveEmployee();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { finishAffinity(); }
        });
    }

    private void showDeactivationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Deactivate Employee")
                .setMessage("Are you sure you want to deactivate this employee?")
                .setPositiveButton("Yes", (dialog, which) -> saveEmployee())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadEmployee() {
        if (businessId == null || businessId.trim().isEmpty()) {
            Toast.makeText(this, "Business id missing (cannot load employee)", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("businesses").document(businessId)
                .collection("employees").document(employeeUid)
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    if (doc.exists()) {
                        // safe read of fields
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String phone = doc.getString("phone");
                        String role = doc.getString("role");
                        Boolean isActiveBool = doc.getBoolean("isActive"); // safe boolean read

                        etName.setText(name != null ? name : "");
                        etEmail.setText(email != null ? email : "");
                        etPhone.setText(phone != null ? phone : "");

                        originalIsActive = (isActiveBool != null && isActiveBool);
                        cbIsActive.setChecked(originalIsActive);

                        tvStatus.setText(originalIsActive ? "Active" : "Disabled");
                        tvStatus.setTextColor(
                                originalIsActive ? ContextCompat.getColor(this, R.color.green)
                                        : ContextCompat.getColor(this, R.color.red)
                        );

                        // preselect role in spinner
                        String[] roles = getResources().getStringArray(R.array.employee_role);
                        if (role != null) {
                            for (int i = 0; i < roles.length; i++) {
                                if (roles[i].equalsIgnoreCase(role)) {
                                    spinnerRole.setSelection(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Employee not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(err -> Toast.makeText(this, "Load error: " + err.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveEmployee() {
        String name = etName.getText().toString().trim();
        String role = spinnerRole.getSelectedItem() != null ? spinnerRole.getSelectedItem().toString() : "";
        boolean isActive = cbIsActive.isChecked();

        if (TextUtils.isEmpty(role)) {
            Toast.makeText(this, "Role is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("role", role);
        update.put("isActive", isActive);
        update.put("updatedAt", com.google.firebase.Timestamp.now());
        if (mAuth.getCurrentUser() != null) update.put("updatedBy", mAuth.getUid());

        if (businessId == null || businessId.trim().isEmpty()) {
            Toast.makeText(this, "Business id missing (cannot save)", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("businesses").document(businessId)
                .collection("employees").document(employeeUid)
                .update(update)
                .addOnSuccessListener(aVoid -> {
                    // update users/{uid}.role (best-effort)
                    db.collection("users").document(employeeUid).update("role", role);
                    Toast.makeText(this, "Employee updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(err -> Toast.makeText(this, "Update error: " + err.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
