package com.example.tlotlotau;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Auth.LoginActivity;

import com.example.tlotlotau.Auth.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {


    private Button btnRegister;

    private Button btnLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_LONG).show();
        }



        //Bottom Navigation Logic


        // Initialize buttons (make sure these IDs exist in your activity_main.xml)

        btnRegister = findViewById(R.id.btnRegister);

        btnLogin = findViewById(R.id.btnLogin);


        // Set click listeners for each button

        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        // Profile Button checks if user exists first

        // Initialize the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    }
}
