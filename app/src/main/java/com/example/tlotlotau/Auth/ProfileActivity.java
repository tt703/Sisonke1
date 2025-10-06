package com.example.tlotlotau.Auth;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tlotlotau.Main.MainActivity;
import com.example.tlotlotau.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView profileImage;
    EditText displayName;
    Button btnUpdatePass, btnDeleteUser;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String businessId;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // init firebase + ui
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.profileImage);
        displayName = findViewById(R.id.displayName);
        btnUpdatePass = findViewById(R.id.btnUpdatePass);
        btnDeleteUser = findViewById(R.id.btn_delete_user);
        btnBack = findViewById(R.id.btnBack);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("User Profile");

        // load cached businessId (optional) - use same key your app uses elsewhere
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessId = prefs.getString("businessId", null);

        boolean force = getIntent().getBooleanExtra("forceChange", false);
        if (force) {
            // show update dialog immediately and prevent skipping
            updatePasswordDialog();
        }


        // current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not signed in. Please login.", Toast.LENGTH_LONG).show();
            // redirect to login or main
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        currentUid = currentUser.getUid();

        // if no businessId in prefs, fallback to currentUid (owner = businessId)
        if (businessId == null || businessId.trim().isEmpty()) {
            businessId = currentUid;
        }

        // populate UI
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(profileImage);
        }
        displayName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "");

        btnBack.setOnClickListener(v -> finish());

        btnUpdatePass.setOnClickListener(v -> updatePasswordDialog());

        btnDeleteUser.setOnClickListener(v -> deleteUserDialog());

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });
    }
    private void updatePasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.update_password);

        EditText et_email = dialog.findViewById(R.id.et_email);
        EditText et_password = dialog.findViewById(R.id.et_password);
        EditText et_new_password = dialog.findViewById(R.id.et_new_password);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        ImageButton finish = dialog.findViewById(R.id.finish);

        // Pre-fill email with current user email
        FirebaseUser u = mAuth.getCurrentUser();
        if (u != null && u.getEmail() != null) {
            et_email.setText(u.getEmail());
        }

        finish.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {
            String email = et_email.getText().toString().trim();
            String currentPassword = et_password.getText().toString();
            String newPassword = et_new_password.getText().toString();

            if (email.isEmpty()) {
                et_email.setError("Please enter your email");
                return;
            }
            if (currentPassword.isEmpty()) {
                et_password.setError("Please enter your current password");
                return;
            }
            if (newPassword.isEmpty()) {
                et_new_password.setError("Please enter a new password");
                return;
            }
            if (currentPassword.equals(newPassword)) {
                Toast.makeText(this, "New password cannot be the same as the current password", Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "User not authenticated. Please login again.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }

            // Reauthenticate
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Reauth succeeded -> update password
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            // Update Firestore documents to mark mustChangePassword=false and activate user
                            Map<String, Object> empUpdate = new HashMap<>();
                            empUpdate.put("mustChangePassword", false);
                            empUpdate.put("isActive", true);
                            empUpdate.put("updatedAt", com.google.firebase.Timestamp.now());

                            // Update employee doc under businesses/{businessId}/employees/{uid}
                            db.collection("businesses").document(businessId)
                                    .collection("employees").document(currentUid)
                                    .update(empUpdate)
                                    .addOnCompleteListener(empUpdateTask -> {
                                        // Best-effort update to users/{uid}.status
                                        Map<String, Object> userUpdate = new HashMap<>();
                                        userUpdate.put("status", "active");
                                        db.collection("users").document(currentUid)
                                                .update(userUpdate)
                                                .addOnCompleteListener(uTask -> {
                                                    // Both updates are best-effort — notify user regardless
                                                    Toast.makeText(ProfileActivity.this, "Password updated and account activated", Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // users doc update failed but password changed; inform user
                                                    Toast.makeText(ProfileActivity.this, "Password updated but failed to update users status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        // employee doc update failed
                                        Toast.makeText(ProfileActivity.this, "Password changed but failed to update employee record: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    });

                        } else {
                            String err = updateTask.getException() != null ? updateTask.getException().getMessage() : "Failed to update password";
                            Toast.makeText(ProfileActivity.this, err, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    String err = task.getException() != null ? task.getException().getMessage() : "Re-authentication failed";
                    Toast.makeText(ProfileActivity.this, err, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void deleteUserDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_user);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // don't force layout sizes here; let the dialog manage it

        EditText del_email = dialog.findViewById(R.id.del_email);
        EditText del_password = dialog.findViewById(R.id.del_password);
        Button btn_delete_user = dialog.findViewById(R.id.btn_delete_user);
        ImageButton finish = dialog.findViewById(R.id.finish);

        // Prefill email
        FirebaseUser u = mAuth.getCurrentUser();
        if (u != null && u.getEmail() != null) del_email.setText(u.getEmail());

        finish.setOnClickListener(v -> dialog.dismiss());

        btn_delete_user.setOnClickListener(v -> {
            String email = del_email.getText().toString().trim();
            String password = del_password.getText().toString();
            if (email.isEmpty()) {
                del_email.setError("Please enter your email");
                return;
            }
            if (password.isEmpty()) {
                del_password.setError("Please enter your password");
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                Toast.makeText(ProfileActivity.this, "No authenticated user", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
            user.reauthenticate(credential).addOnCompleteListener((@NonNull Task<Void> task) -> {
                if (task.isSuccessful()) {
                    user.delete().addOnCompleteListener(delTask -> {
                        if (delTask.isSuccessful()) {
                            // Optionally clean Firestore documents (best-effort)
                            // db.collection("users").document(user.getUid()).delete(); etc.
                            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to delete account: " + (delTask.getException() != null ? delTask.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Re-authentication failed: " + (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }
}
