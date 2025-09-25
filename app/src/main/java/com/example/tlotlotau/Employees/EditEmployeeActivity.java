package com.example.tlotlotau.Employees;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditEmployeeActivity extends AppCompatActivity {
    EditText etName, etEmail, etPhone,etRole;
    ImageButton btnBack;
    CheckBox cbIsActive;
    Button btnSave;
    String employeeUid;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        etName = findViewById(R.id.etEmpName);
        etEmail = findViewById(R.id.etEmpEmail);
        etPhone = findViewById(R.id.etEmpPhone);
        etRole = findViewById(R.id.etEmpRole);
        cbIsActive = findViewById(R.id.cbEmpActive);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        employeeUid = getIntent().getStringExtra("employeeUid");
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessId = prefs.getString("business_id", auth.getUid());
        if(TextUtils.isEmpty(employeeUid)){
            finish();
            return;
        }
        loadEmployee();
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveEmployee());
    }
    private void loadEmployee(){
        db.collection("businesses").document(businessId)
                .collection("employees").document(employeeUid)
                .get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        Employee e = doc.toObject(Employee.class);
                        etName.setText(e.name);
                        etEmail.setText(e.email);
                        etPhone.setText(e.phone);
                        etRole.setText(e.role);
                        cbIsActive.setChecked(e.isActive);
                    } else{
                        Toast.makeText(this, "Employee not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(err -> Toast.makeText(this,"Load error:"+ err.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void saveEmployee(){
        String name = etName.getText().toString();
        String role = etRole.getText().toString();
        boolean isActive = cbIsActive.isChecked();

        if(TextUtils.isEmpty(role)){
            etRole.setError("Role is required");
            return;
        }
        Map<String, Object> update = new HashMap<>();
        update.put("name",name);
        update.put("role",role);
        update.put("isActive",isActive);

        db.collection("businesses").document(businessId)
                .collection("employees").document(employeeUid)
                .update(update)
                .addOnSuccessListener(aVoid ->{
                    //also update users/{uid}.role
                    db.collection("users").document(employeeUid).update("role",role);
                    Toast.makeText(this,"Employee updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(err -> Toast.makeText(this,"Update error:"+ err.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
