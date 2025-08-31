package com.example.tlotlotau.Auth;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.MainActivity;
import com.example.tlotlotau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;


public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText name,email, Password;
    Button btnRegisterUser;
    ImageButton btnBack;
    TextView login;

    String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String PasswordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        //back header
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, MainActivity.class)));


        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        btnRegisterUser = findViewById(R.id.btnRegisterUser);

        //Email format validation
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b && !email.getText().toString().matches(EmailPattern)){
                    email.setError("Invalid Email");
                }
            }
        });

        //Password format validation
        Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b && !Password.getText().toString().matches(PasswordPattern)){
                    Password.setError("Invalid Password");
                }
            }
        });




        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please enter name");
                } else if (email.getText().toString().isEmpty()) {
                    email.setError("Please enter email");
                } else if (Password.getText().toString().isEmpty()) {
                    Password.setError("Please enter password");
                } else {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), Password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name.getText().toString())
                                                .setPhotoUri(Uri.parse("https://freerangestock.com/smaple/120147/business-man-profile-vector.jpg"))
                                                .build();

                                        mAuth.getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(),"User has been registered successfully", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                }
            }
        });
    }
}
