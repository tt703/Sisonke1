package com.example.tlotlotau.Settings;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.R;


public class PaymentMethod extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnSaveCard, btnSaveInfo;
    private EditText etCardNumber, etExpiry, etCvc, etFullName, etAccountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        btnBack = findViewById(R.id.btnBack);
        btnSaveCard = findViewById(R.id.btnSaveCard);
        btnSaveInfo = findViewById(R.id.btnSaveInfo);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvc = findViewById(R.id.etCvc);

        etCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean b) {

            }

        });

    }
}