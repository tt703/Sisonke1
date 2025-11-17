package com.example.tlotlotau.Sales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.R;
import com.google.android.material.button.MaterialButton;

public class PaymentSelectionActivity extends AppCompatActivity {

    private MaterialButton btnCard, btnCash;
    private TextView tvSubtotal, tvTax, tvTotal, tvItemCount;
    private ImageButton btnBack;

    private static final double TAX_PERCENT = 15.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_selection);

        // views
        btnCard = findViewById(R.id.btnPayCard);
        btnCash = findViewById(R.id.btnPayCash);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);
        tvItemCount = findViewById(R.id.tvItemCount);
        btnBack = findViewById(R.id.btnBack);

        // back
        btnBack.setOnClickListener(v -> onBackPressed());

        // card/cash actions
        btnCard.setOnClickListener(v -> {
            // guard: disable if cart empty
            if (Cart.get().isEmpty()) return;
            startActivity(new Intent(this, CardPaymentActivity.class));
        });

        btnCash.setOnClickListener(v -> {
            if (Cart.get().isEmpty()) return;
            startActivity(new Intent(this, CashPaymentActivity.class));
        });

        // initial fill
        refreshTotalsAndControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTotalsAndControls();
    }

    private void refreshTotalsAndControls() {
        double subtotal = Cart.get().subtotal();
        double tax = Cart.get().tax(TAX_PERCENT);
        double total = Cart.get().total(TAX_PERCENT);
        int count = Cart.get().getItems().size();

        tvSubtotal.setText(String.format("R %.2f", subtotal));
        tvTax.setText(String.format("R %.2f", tax));
        tvTotal.setText(String.format("R %.2f", total));
        tvItemCount.setText(count + (count == 1 ? " item" : " items"));

        boolean hasItems = count > 0;

        btnCard.setEnabled(hasItems);
        btnCash.setEnabled(hasItems);

        btnCard.setAlpha(hasItems ? 1f : 0.6f);
        btnCash.setAlpha(hasItems ? 1f : 0.6f);
    }
}
