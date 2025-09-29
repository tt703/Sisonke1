package com.example.tlotlotau.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlotlotau.Main.HomeActivity;
import com.example.tlotlotau.Main.MainActivity;
import com.example.tlotlotau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private Button btnLoginUser;

    private TextView verify_email;
    private TextView forgot_password;
    private ImageButton btnBack;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));

        mAuth = FirebaseAuth.getInstance();

        // If a user is currently signed in for some reason, we sign out to show the login screen fresh
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLoginUser = findViewById(R.id.btnLoginUser);
        verify_email = findViewById(R.id.verify_email);
        forgot_password = findViewById(R.id.forgot_password);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        businessId = prefs.getString("business_id", auth.getUid());

        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter your Email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter your Password", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
                                    // User signed in and verified. Now check role & employee flags.
                                    final String uid = mAuth.getCurrentUser().getUid();

                                    db.collection("users").document(uid)
                                            .get()
                                            .addOnSuccessListener(userDoc -> {
                                                if (userDoc.exists()) {
                                                    String role = userDoc.getString("role");
                                                    String businessIdFromUserDoc = userDoc.getString("businessId");
                                                    final String effectiveBusinessId = (businessIdFromUserDoc == null) ? uid : businessIdFromUserDoc;

                                                    // If role is employee, check employee doc flags
                                                    if ("employee".equalsIgnoreCase(role)) {
                                                        db.collection("businesses").document(effectiveBusinessId)
                                                                .collection("employees")
                                                                .document(uid)
                                                                .get()
                                                                .addOnSuccessListener(empDoc -> {
                                                                    if (empDoc.exists()) {
                                                                        // read isActive first: block if explicitly false
                                                                        Boolean isActive = empDoc.getBoolean("isActive");
                                                                        if (Boolean.FALSE.equals(isActive)) {
                                                                            // Sign out the just-signed-in user and block
                                                                            mAuth.signOut();
                                                                            Toast.makeText(LoginActivity.this, "Your account has been deactivated. Contact the owner.", Toast.LENGTH_LONG).show();
                                                                            return;
                                                                        }

                                                                        // read mustChangePassword and handle forcing password change
                                                                        Boolean mustChange = empDoc.getBoolean("mustChangePassword");
                                                                        if (Boolean.TRUE.equals(mustChange)) {
                                                                            Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                                                                            i.putExtra("forceChange", true);
                                                                            startActivity(i);
                                                                            finish();
                                                                            return;
                                                                        }
                                                                    }
                                                                    // If employee doc missing or flags permit, proceed to Home
                                                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                                    finish();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    // Couldn't read employee doc — sign out for safety or allow with warning.
                                                                    mAuth.signOut();
                                                                    Toast.makeText(getApplicationContext(), "Login succeeded but failed to read employee data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                });
                                                    } else {
                                                        // Not an employee: just proceed to Home
                                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                        finish();
                                                    }
                                                } else {
                                                    // No users/{uid} doc -> allow sign-in (or you may want to block)
                                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                mAuth.signOut();
                                                Toast.makeText(getApplicationContext(), "Failed to load user info: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });

                                } else {
                                    Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException() != null ? task.getException().getMessage() : "Login failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        forgot_password.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()) {
                email.setError("Please Enter your Email");
            } else {
                mAuth.sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Password Reset Email Sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send reset email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        verify_email.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(getApplicationContext(), "Please Sign In First", Toast.LENGTH_LONG).show();
            } else {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Verification Email Sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send verification", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });
    }
}
