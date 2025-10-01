package com.example.tlotlotau.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Auth.LoginActivity;
import com.example.tlotlotau.Auth.ProfileActivity;
import com.example.tlotlotau.Employees.ManageEmployeesActivity;
import com.example.tlotlotau.Main.HomeActivity;
import com.example.tlotlotau.R;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {
    private ImageButton btnProfile;
    private ImageButton btnEditCompany;
    private ImageButton btnPayment;
    private ImageButton btnEmployees;
    private ImageButton btnBack;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        btnProfile = findViewById(R.id.btnProfile);
        btnEditCompany = findViewById(R.id.btnEditCompany);
        btnPayment = findViewById(R.id.btnPayment);
        btnEmployees = findViewById(R.id.btnEmployees);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btn_logout);


        btnBack.setOnClickListener(view -> startActivity(new Intent(Settings.this, HomeActivity.class)));

        btnProfile.setOnClickListener(view -> 
            startActivity(new Intent(Settings.this, ProfileActivity.class))
        );

        btnEditCompany.setOnClickListener(view -> 
            startActivity(new Intent(Settings.this, EditCompanyInfoActivity.class))
        );
        btnPayment.setOnClickListener(view ->
            startActivity(new Intent(Settings.this, PaymentMethod.class))
        );
        btnEmployees.setOnClickListener(view ->
          startActivity(new Intent(Settings.this, ManageEmployeesActivity.class))
        );
        btnLogout.setOnClickListener(view -> logout());

    }
    private void logout() {

        FirebaseAuth.getInstance().signOut();
        // go to login screen
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
