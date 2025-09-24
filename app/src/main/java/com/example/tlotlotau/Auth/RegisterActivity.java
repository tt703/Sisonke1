package com.example.tlotlotau.Auth;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.MainActivity;
import com.example.tlotlotau.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText name, email, Password;
    Button btnRegisterUser;
    ImageButton btnBack;
    TextView login;
    // Regular expressions for email and password validation
    String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String PasswordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, MainActivity.class)));

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        btnRegisterUser = findViewById(R.id.btnRegisterUser);

        email.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String emailInput = email.getText().toString().trim();
                if (TextUtils.isEmpty(emailInput)) {
                    email.setError("Email cannot be empty");
                } else if (!emailInput.matches(EmailPattern)) {
                    email.setError("Invalid Email format");
                } else {
                    email.setError(null);
                }
            }
        });

        Password.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String passwordInput = Password.getText().toString().trim();
                if (TextUtils.isEmpty(passwordInput)) {
                    Password.setError("Password cannot be empty");
                } else if (!passwordInput.matches(PasswordPattern)) {
                    Password.setError("Password must be at least 4 chars, include uppercase, lowercase, digit, and special char (@#$%^&+=!)");
                } else {
                    Password.setError(null);
                }
            }
        });

        btnRegisterUser.setOnClickListener(view -> {
            String userName = name.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPassword = Password.getText().toString().trim();

            boolean validInputs = true;

            if (TextUtils.isEmpty(userName)) {
                name.setError("Please enter name");
                validInputs = false;
            }
            if (TextUtils.isEmpty(userEmail)) {
                email.setError("Please enter email");
                validInputs = false;
            } else if (!userEmail.matches(EmailPattern)) {
                email.setError("Invalid Email format");
                validInputs = false;
            }
            if (TextUtils.isEmpty(userPassword)) {
                Password.setError("Please enter password");
                validInputs = false;
            } else if (!userPassword.matches(PasswordPattern)) {
                Password.setError("Password must be at least 4 chars, include uppercase, lowercase, digit, and special char (@#$%^&+=!)");
                validInputs = false;
            }

            if (!validInputs) {
                Toast.makeText(getApplicationContext(), "Please correct the errors", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), Password.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            // Update FirebaseAuth profile
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.getText().toString())
                                    .setPhotoUri(Uri.parse("https://freerangestock.com/smaple/120147/business-man-profile-vector.jpg"))
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileChangeRequest);

                            // --- FIRESTORE: Add user document ---
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> user = new HashMap<>();
                            user.put("uid", uid);
                            user.put("name", name.getText().toString());
                            user.put("email", email.getText().toString());
                            user.put("role", "owner");            // important for RBAC
                            user.put("businessId", uid);          // owner uid = businessId
                            user.put("status", "active");
                            user.put("createdAt", FieldValue.serverTimestamp());

                            db.collection("users").document(uid)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        // --- FIRESTORE: Create empty business document ---
                                        Map<String, Object> business = new HashMap<>();
                                        business.put("ownerId", uid);
                                        business.put("createdAt", FieldValue.serverTimestamp());

                                        db.collection("businesses").document(uid)
                                                .set(business)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Toast.makeText(getApplicationContext(),"Owner registered successfully", Toast.LENGTH_LONG).show();
                                                    // Navigate to Edit Company Details page
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                    finish();
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(getApplicationContext(), "Error creating business: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                                );

                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getApplicationContext(), "Error creating user: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );

                        } else {
                            Toast.makeText(getApplicationContext(), "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}

