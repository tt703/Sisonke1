package com.example.tlotlotau.Employees;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText etEmployeeName, etEmployeePhone, etEmployeeEmail, etEmployeePass;
    private Spinner etEmployeeRole;
    private Button btnSaveEmployee;

    private FirebaseAuth primaryAuth;
    private FirebaseFirestore db;
    private String businessId;


    private static final long LOCK_TIMEOUT_MS = 60_000L; // 60s lock timeout
    private static final String LOCK_DOC_ID = "employee_creation";

    // simple patterns
    String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String PhonePattern = "[0-9]{10}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeePhone = findViewById(R.id.etEmployeePhone);
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail);
        etEmployeeRole = findViewById(R.id.etEmployeeRole);
        etEmployeePass = findViewById(R.id.etEmployeePass);
        btnSaveEmployee = findViewById(R.id.btnSaveEmployee);

        // roles
        String[] roles = new String[]{"Sales", "Stock Control", "Supervisor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etEmployeeRole.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        primaryAuth = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessId = prefs.getString("businessId", primaryAuth.getUid());
        if (businessId == null) businessId = primaryAuth.getUid();

        btnSaveEmployee.setOnClickListener(v -> saveEmployee());
    }

    private void saveEmployee() {
        final String employeeName = etEmployeeName.getText().toString().trim();
        final String employeePhone = etEmployeePhone.getText().toString().trim();
        final String employeeEmail = etEmployeeEmail.getText().toString().trim();
        final String employeeRole = etEmployeeRole.getSelectedItem().toString().trim();
        final String employeePass = etEmployeePass.getText().toString().trim();

        if (TextUtils.isEmpty(employeeName) || TextUtils.isEmpty(employeePhone)
                || TextUtils.isEmpty(employeeEmail) || TextUtils.isEmpty(employeeRole)
                || TextUtils.isEmpty(employeePass)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!employeePhone.matches(PhonePattern)) {
            Toast.makeText(this, "Invalid Phone Number (expected 10 digits)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!employeeEmail.matches(EmailPattern)) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }

        // this function acquire's a  lock, then run's the create auth user creation function
        acquireCreationLock(primaryAuth.getUid(), (acquired, message) -> {
            if (!acquired) {
                Toast.makeText(AddEmployeeActivity.this, "Failed to acquire lock: " + message, Toast.LENGTH_LONG).show();
                return;
            }

            // this functions Proceeds to create auth user with secondary app and it pass the strings below
            createAuthUserWithSecondaryApp(employeeName, employeePhone, employeeEmail, employeePass, employeeRole);
        });
    }

    // Lock callback
    private interface LockCallback {
        void onResult(boolean acquired, String message);
    }

    // Acquire lock doc using transaction. Returns boolean via callback.
    private void acquireCreationLock(String ownerUid, LockCallback callback) {
        final DocumentReference lockRef = db.collection("businesses").document(businessId)
                .collection("locks").document(LOCK_DOC_ID);

        db.runTransaction((Transaction.Function<Boolean>) transaction -> {
            long now = System.currentTimeMillis();
            DocumentSnapshot snap = transaction.get(lockRef);
            if (snap.exists()) {
                Long lockedAt = snap.getLong("lockedAt");
                if (lockedAt != null && (now - lockedAt) < LOCK_TIMEOUT_MS) {
                    // still locked , this is to avoid race conditions
                    return false;
                }
            }
            Map<String, Object> lock = new HashMap<>();
            lock.put("lockedBy", ownerUid);
            lock.put("lockedAt", System.currentTimeMillis());
            lock.put("createdAt", FieldValue.serverTimestamp());
            transaction.set(lockRef, lock);
            return true;
        }).addOnSuccessListener(acquired -> {
            if (acquired) callback.onResult(true, "Lock acquired");
            else callback.onResult(false, "Another creation in progress; try again in a moment.");
        }).addOnFailureListener(e -> {
            callback.onResult(false, "Lock transaction failed: " + e.getMessage());
        });
    }

    // this function creates an auth user using a secondary FirebaseApp so the primary auth owner remains signed in
    private void createAuthUserWithSecondaryApp(String name, String phone, String email, String pass, String role) {
        final String SECONDARY_APP_NAME = "secondary_" + System.currentTimeMillis();

        FirebaseOptions options;
        try {
            options = FirebaseApp.getInstance().getOptions();
        } catch (Exception e) {
            releaseLockAndShowError("Failed to obtain FirebaseOptions: " + e.getMessage());
            return;
        }

        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.initializeApp(this, options, SECONDARY_APP_NAME);
        } catch (IllegalStateException ise) {
            try {
                secondaryApp = FirebaseApp.getInstance(SECONDARY_APP_NAME);
            } catch (Exception ex) {
                releaseLockAndShowError("Failed to initialize secondary FirebaseApp: " + ex.getMessage());
                return;
            }
        } catch (Exception ex) {
            releaseLockAndShowError("Failed to initialize secondary FirebaseApp: " + ex.getMessage());
            return;
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        // this creates an account on secondaryAuth,we use this to avoid cancelling the owners session
        FirebaseApp finalSecondaryApp = secondaryApp;
        secondaryAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()) {
                            String err = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            // cleanup secondary app
                            try { finalSecondaryApp.delete(); } catch (Exception ignored) {}
                            releaseLockAndShowError("Failed to create auth user: " + err);
                            return;
                        }

                        // success
                        FirebaseUser createdUser = secondaryAuth.getCurrentUser();
                        if (createdUser == null) {
                            try { finalSecondaryApp.delete(); } catch (Exception ignored) {}
                            releaseLockAndShowError("Auth created but could not obtain user");
                            return;
                        }

                        String uid = createdUser.getUid();
                        // write Firestore docs
                        writeEmployeeAndUserDocs(uid, name, phone, email, role, pass, secondaryAuth, finalSecondaryApp);
                    }
                });
    }

    private void writeEmployeeAndUserDocs(String uid,
                                          String employeeName,
                                          String employeePhone,
                                          String employeeEmail,
                                          String employeeRole,
                                          String tempPassword,
                                          FirebaseAuth secondaryAuth,
                                          FirebaseApp secondaryApp) {
        Map<String, Object> emp = new HashMap<>();
        emp.put("uid", uid);
        emp.put("name", employeeName);
        emp.put("phone", employeePhone);
        emp.put("email", employeeEmail);
        emp.put("role", employeeRole);
        emp.put("isActive", true);
        emp.put("mustChangePassword", true);
        emp.put("createdAt", Timestamp.now());
        emp.put("createdBy", primaryAuth.getUid());

        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("name", employeeName);
        user.put("email", employeeEmail);
        user.put("role", "employee");
        user.put("businessId", businessId);
        user.put("status", "pending");
        user.put("createdAt", Timestamp.now());

        db.collection("businesses").document(businessId)
                .collection("employees").document(uid)
                .set(emp)
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid1 -> {
                                // success: share creds and cleanup
                                shareCredentialsAndCleanup(employeeEmail, tempPassword, secondaryAuth, secondaryApp);
                                releaseLock();
                            })
                            .addOnFailureListener(e2 -> {
                                // failed writing users doc -> try delete created auth user
                                attemptDeleteCreatedAuthUserAndCleanup(secondaryAuth, secondaryApp,
                                        "Failed to write users doc: " + e2.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // failed writing employee doc -> try delete created auth user
                    attemptDeleteCreatedAuthUserAndCleanup(secondaryAuth, secondaryApp,
                            "Failed to write employee doc: " + e.getMessage());
                });
    }

    private void shareCredentialsAndCleanup(String email, String tempPassword,
                                            FirebaseAuth secondaryAuth, FirebaseApp secondaryApp) {
        String company = getSharedPreferences("CompanyInfo", MODE_PRIVATE).getString("CompanyName", "Company");
        String message = "You have been added to " + company + ".\n\nEmail: " + email + "\nTemporary password: " + tempPassword +
                "\n\nPlease change your password after logging in.";

        Intent send = new Intent(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_TEXT, message);
        send.setType("text/plain");
        startActivity(Intent.createChooser(send, "Share credentials via"));

        // cleanup: sign out secondary auth and delete app (best effort)
        try { secondaryAuth.signOut(); } catch (Exception ignored) {}
        try { secondaryApp.delete(); } catch (Exception ignored) {}

        Toast.makeText(this, "Employee created", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void attemptDeleteCreatedAuthUserAndCleanup(FirebaseAuth secondaryAuth, FirebaseApp secondaryApp, String ownerMessage) {
        FirebaseUser toDelete = secondaryAuth.getCurrentUser();
        if (toDelete != null) {
            toDelete.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    releaseLock();
                    Toast.makeText(AddEmployeeActivity.this, ownerMessage + " — created auth user deleted.", Toast.LENGTH_LONG).show();
                } else {
                    releaseLock();
                    Toast.makeText(AddEmployeeActivity.this, ownerMessage + " — failed to delete created auth user.", Toast.LENGTH_LONG).show();
                }
                try { secondaryAuth.signOut(); } catch (Exception ignored) {}
                try { secondaryApp.delete(); } catch (Exception ignored) {}
            });
        } else {
            releaseLock();
            try { secondaryAuth.signOut(); } catch (Exception ignored) {}
            try { secondaryApp.delete(); } catch (Exception ignored) {}
            Toast.makeText(this, ownerMessage + " — no auth user to delete.", Toast.LENGTH_LONG).show();
        }
    }

    private void releaseLock() {
        DocumentReference lockRef = db.collection("businesses").document(businessId)
                .collection("locks").document(LOCK_DOC_ID);
        lockRef.delete();
    }

    private void releaseLockAndShowError(String message) {
        releaseLock();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
