package com.example.tlotlotau.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tlotlotau.Main.HomeActivity;
import com.example.tlotlotau.Main.MainActivity;
import com.example.tlotlotau.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private Button btnLoginUser;
    private TextView verify_email;
    private TextView forgot_password;
    private ImageButton btnBack;

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


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLoginUser = findViewById(R.id.btnLoginUser);
        verify_email = findViewById(R.id.verify_email);
        forgot_password = findViewById(R.id.forgot_password);

        btnLoginUser.setOnClickListener(v -> attemptLogin());

        forgot_password.setOnClickListener(v -> {
            String e = email.getText().toString().trim();
            if (e.isEmpty()) {
                email.setError("Please Enter your Email");
                return;
            }
            mAuth.sendPasswordResetEmail(e).addOnCompleteListener((Task<Void> task) -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Password Reset Email Sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to send reset email", Toast.LENGTH_LONG).show();
                }
            });
        });

        verify_email.setOnClickListener(v -> {
            // Only attempt to send verification if there is a signed-in user
            FirebaseUser cur = mAuth.getCurrentUser();
            if (cur == null) {
                Toast.makeText(getApplicationContext(), "Please sign in first to request verification.", Toast.LENGTH_LONG).show();
                return;
            }

            // reload to ensure up-to-date state then send verification
            cur.reload().addOnCompleteListener(rt -> {
                cur.sendEmailVerification().addOnCompleteListener(sendTask -> {
                    if (sendTask.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Verification Email Sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                        // sign out so user must verify before continuing
                        mAuth.signOut();
                    } else {
                        String err = (sendTask.getException() != null) ? sendTask.getException().getMessage() : "Failed to send verification";
                        Toast.makeText(getApplicationContext(), "Failed to send verification: " + err, Toast.LENGTH_LONG).show();
                    }
                });
            });
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finishAffinity(); }
        });
    }

    private void attemptLogin() {
        final String e = email.getText().toString().trim();
        final String p = password.getText().toString();

        if (e.isEmpty()) { Toast.makeText(getApplicationContext(), "Please Enter your Email", Toast.LENGTH_LONG).show(); return; }
        if (p.isEmpty()) { Toast.makeText(getApplicationContext(), "Please Enter your Password", Toast.LENGTH_LONG).show(); return; }

        btnLoginUser.setEnabled(false);

        mAuth.signInWithEmailAndPassword(e, p)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    btnLoginUser.setEnabled(true);

                    if (!task.isSuccessful()) {
                        String msg = (task.getException() != null) ? task.getException().getMessage() : "Login failed";
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Get the signed-in user from the AuthResult
                    if (task.getResult() == null || task.getResult().getUser() == null) {
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), "Login failed (no user returned).", Toast.LENGTH_LONG).show();
                        return;
                    }

                    final FirebaseUser signedIn = task.getResult().getUser();

                    // reload to ensure emailVerified value is fresh from server
                    signedIn.reload().addOnCompleteListener(reloadTask -> {
                        if (!reloadTask.isSuccessful()) {
                            // reload failed — be conservative: sign out and show message
                            mAuth.signOut();
                            String msg = (reloadTask.getException() != null) ? reloadTask.getException().getMessage() : "Failed to verify login state";
                            Toast.makeText(getApplicationContext(), "Failed to verify login: " + msg, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!signedIn.isEmailVerified()) {
                            signedIn.sendEmailVerification().addOnCompleteListener(sendTask -> {
                                if (sendTask.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Email not verified.Please Verify Email.", Toast.LENGTH_LONG).show();
                                } else {
                                    String err = (sendTask.getException() != null) ? sendTask.getException().getMessage() : "Failed to send verification";
                                    Toast.makeText(LoginActivity.this, "Email not verified. Failed to send verification: " + err, Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    });
                });
    }
}
