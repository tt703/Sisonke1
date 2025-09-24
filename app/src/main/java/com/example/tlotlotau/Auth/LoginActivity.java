package com.example.tlotlotau.Auth;

import static com.itextpdf.kernel.pdf.PdfName.View;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.bumptech.glide.Glide;
import com.example.tlotlotau.HomeActivity;
import com.example.tlotlotau.MainActivity;
import com.example.tlotlotau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
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
        String username = getIntent().getStringExtra("USERNAME_KEY");
        if (username != null) {
            // Pre-fill the username field
        }
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLoginUser = findViewById(R.id.btnLoginUser);
        verify_email = findViewById(R.id.verify_email);
        forgot_password = findViewById(R.id.forgot_password);


        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter your Email", Toast.LENGTH_LONG).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter your Password", Toast.LENGTH_LONG).show();

                } else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (mAuth.getCurrentUser().isEmailVerified()) {
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }


            }

        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty()) {
                    email.setError("Please Enter your Email");
                } else {
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password Reset Email Sent", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });
        verify_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "Please Sign In First", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Verification Email Sent", Toast.LENGTH_LONG).show();
                            }

                        }

                    });
                }
            }


        });
        //Remember Me and gold when checked
        CheckBox rememberMe = findViewById(R.id.rememberMe);

        // Define color states for the CheckBox
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked}, // checked
                        new int[]{}  // default (unchecked)
                },
                new int[]{
                        ContextCompat.getColor(this, R.color.gold), // gold for checked state
                        ContextCompat.getColor(this, android.R.color.darker_gray) // default color for unchecked state
                }
        );
        CompoundButtonCompat.setButtonTintList(rememberMe, colorStateList);

        rememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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