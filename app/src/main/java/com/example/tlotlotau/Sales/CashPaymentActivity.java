package com.example.tlotlotau.Sales;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class CashPaymentActivity extends AppCompatActivity {

    private TextView tvCashAmount;
    private TextView tvCashTax;
    private TextView tvCashTotal;
    private TextInputEditText etReceived;
    private TextView tvChange;
    private MaterialButton btnConfirm;
    private MaterialButton btnCancel;
    private ImageButton btnBack;

    private static final double TAX_PERCENT = 15.0;
    private final DecimalFormat money = new DecimalFormat("R #,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);

        // views
        tvCashAmount = findViewById(R.id.tvCashAmount);
        tvCashTax = findViewById(R.id.tvCashTax);
        tvCashTotal = findViewById(R.id.tvCashTotal);
        etReceived = findViewById(R.id.etCashReceived);
        tvChange = findViewById(R.id.tvChange);
        btnConfirm = findViewById(R.id.btnCashConfirm);
        btnCancel = findViewById(R.id.btnCashCancel);
        btnBack = findViewById(R.id.btnBack);

        // initial values from cart
        double subtotal = Cart.get().subtotal();
        double tax = Cart.get().tax(TAX_PERCENT);
        double total = Cart.get().total(TAX_PERCENT);

        tvCashAmount.setText(money.format(subtotal));
        tvCashTax.setText(money.format(tax));
        tvCashTotal.setText(money.format(total));
        tvChange.setText(money.format(0.0));
        btnConfirm.setEnabled(false); // only enabled once sufficient cash entered

        // back/cancel
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> {
            Toast.makeText(CashPaymentActivity.this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });

        // listen for input changes
        etReceived.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                handleReceivedInput(s == null ? "" : s.toString(), total);
            }
        });

        // confirm handler
        btnConfirm.setOnClickListener(v -> {
            String raw = etReceived.getText() == null ? "" : etReceived.getText().toString().trim();
            if (raw.isEmpty()) {
                Toast.makeText(CashPaymentActivity.this, "Enter amount received", Toast.LENGTH_SHORT).show();
                return;
            }
            double received;
            try {
                received = Double.parseDouble(raw);
            } catch (NumberFormatException ex) {
                Toast.makeText(CashPaymentActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (received < total) {
                Toast.makeText(CashPaymentActivity.this, "Insufficient cash", Toast.LENGTH_SHORT).show();
                return;
            }

            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
            btnBack.setEnabled(false);

            // run DB updates in background thread
            new Thread(() -> {
                boolean success = false;
                DatabaseHelper db = new DatabaseHelper(CashPaymentActivity.this);
                List<SaleItem> items = Cart.get().getItems();
                double subtotalLocal = Cart.get().subtotal();
                double taxLocal = Cart.get().tax(TAX_PERCENT);
                double totalLocal = Cart.get().total(TAX_PERCENT);

                // get stored user info (if any)
                SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                String userId = sp.getString("user_id", "");
                String userName = sp.getString("user_name", "");
                String userRole = sp.getString("user_role", "");

                try {
                    // create sale (inserts sale + items and decrements inventory in a transaction)
                    success = db.createSale(userId, userName, userRole, items, subtotalLocal, taxLocal, totalLocal, "CASH");
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }

                double finalReceived = received;
                double change = finalReceived - totalLocal;

                boolean finalSuccess = success;
                runOnUiThread(() -> {
                    if (finalSuccess) {
                        // go to receipt, pass items_text BEFORE clearing cart
                        Intent i = new Intent(CashPaymentActivity.this, ReceiptActivity.class);
                        i.putExtra("payment_method", "CASH");
                        i.putExtra("received", finalReceived);
                        i.putExtra("change", change);
                        i.putExtra("subtotal", subtotalLocal);
                        i.putExtra("tax", taxLocal);
                        i.putExtra("total", totalLocal);
                        i.putExtra("items_text", buildItemsTextForIntent()); // <<< important
                        startActivity(i);

                        // clear cart
                        Cart.get().clear();

                        // finish
                        finish();
                    } else {
                        Toast.makeText(CashPaymentActivity.this, "Failed to record sale. Try again.", Toast.LENGTH_LONG).show();
                        btnConfirm.setEnabled(true);
                        btnCancel.setEnabled(true);
                        btnBack.setEnabled(true);
                    }
                });
            }).start();
        });
    }

    private void handleReceivedInput(String raw, double total) {
        if (raw == null) raw = "";
        raw = raw.trim();
        if (raw.isEmpty()) {
            tvChange.setText(money.format(0.0));
            btnConfirm.setEnabled(false);
            return;
        }
        try {
            double received = Double.parseDouble(raw);
            double change = received - total;
            if (change < 0) {
                // not enough
                tvChange.setText(money.format(change));
                btnConfirm.setEnabled(false);
            } else {
                tvChange.setText(money.format(change));
                btnConfirm.setEnabled(true);
            }
        } catch (NumberFormatException ex) {
            // bad input
            tvChange.setText("—");
            btnConfirm.setEnabled(false);
        }
    }

    // Build items_text (pipe-separated lines: name|qty|lineTotal)
    private String buildItemsTextForIntent() {
        StringBuilder sb = new StringBuilder();
        for (SaleItem it : Cart.get().getItems()) {
            sb.append(it.product.getProductName().replace("|"," ")).append("|")
                    .append(it.quantity).append("|")
                    .append(String.format(Locale.getDefault(),"%.2f", it.lineTotal()));
            sb.append("\n");
        }
        return sb.toString();
    }
}
