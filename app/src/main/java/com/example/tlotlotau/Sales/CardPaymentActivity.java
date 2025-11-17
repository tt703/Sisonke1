package com.example.tlotlotau.Sales;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class CardPaymentActivity extends AppCompatActivity {

    private TextView tvAmount;
    private MaterialButton btnApprove;
    private MaterialButton btnCancel;
    private ImageButton btnBack;
    private ProgressBar pbWaiting;
    private TextView tvStatus;
    private ImageView ivStatusIcon;

    private static final double TAX_PERCENT = 15.0;
    private final DecimalFormat money = new DecimalFormat("R #,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        // views
        tvAmount = findViewById(R.id.tvCardAmount);
        btnApprove = findViewById(R.id.btnCardApprove);
        btnCancel = findViewById(R.id.btnCardCancel);
        btnBack = findViewById(R.id.btnBack);
        pbWaiting = findViewById(R.id.pbWaiting);
        tvStatus = findViewById(R.id.tvStatus);
        ivStatusIcon = findViewById(R.id.ivStatusIcon);

        // show current total
        double amount = Cart.get().total(TAX_PERCENT);
        tvAmount.setText(money.format(amount));

        // back button - just finish
        btnBack.setOnClickListener(v -> finish());

        // cancel - just go back
        btnCancel.setOnClickListener(v -> {
            Toast.makeText(CardPaymentActivity.this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });

        // approve
        btnApprove.setOnClickListener(v -> {
            if (Cart.get().isEmpty()) {
                Toast.makeText(CardPaymentActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            startPaymentFlow();
        });
    }

    private void startPaymentFlow() {
        // disable buttons while processing
        setControlsEnabled(false);
        pbWaiting.setVisibility(View.VISIBLE);
        tvStatus.setText("Processing payment — please wait...");
        ivStatusIcon.setImageResource(R.drawable.ic_clock);

        // run DB and stock update on background thread
        new Thread(() -> {
            boolean success = false;
            String method = "CARD";
            DatabaseHelper db = new DatabaseHelper(CardPaymentActivity.this);

            // Gather sale data
            List<SaleItem> items = Cart.get().getItems();
            double subtotal = Cart.get().subtotal();
            double tax = Cart.get().tax(TAX_PERCENT);
            double total = Cart.get().total(TAX_PERCENT);

            // get user info from prefs (if available)
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String userId = sp.getString("user_id", "");
            String userName = sp.getString("user_name", "");
            String userRole = sp.getString("user_role", "");

            try {
                // create sale (this method inserts sale rows and decrements inventory)
                success = db.createSale(userId, userName, userRole, items, subtotal, tax, total, method);

            } catch (Exception ex) {
                ex.printStackTrace();
                success = false;
            }

            final boolean finalSuccess = success;
            runOnUiThread(() -> {
                pbWaiting.setVisibility(View.GONE);
                if (finalSuccess) {
                    tvStatus.setText("Payment approved — printing receipt");
                    ivStatusIcon.setImageResource(R.drawable.ic_check_circle);

                    // navigate to receipt activity and pass items + totals
                    Intent i = new Intent(CardPaymentActivity.this, ReceiptActivity.class);
                    i.putExtra("payment_method", method);
                    i.putExtra("subtotal", subtotal);
                    i.putExtra("tax", tax);
                    i.putExtra("total", total);
                    i.putExtra("items_text", buildItemsTextForIntent()); // <<< important

                    // start receipt BEFORE clearing cart so receipt has data even if cart cleared
                    startActivity(i);

                    // clear cart now that sale is persisted
                    Cart.get().clear();

                    // finish this activity (so back goes to sell screen)
                    finish();
                } else {
                    tvStatus.setText("Payment failed. Try again.");
                    ivStatusIcon.setImageResource(R.drawable.ic_close);
                    Toast.makeText(CardPaymentActivity.this, "Failed to register sale. Check logs.", Toast.LENGTH_LONG).show();
                    setControlsEnabled(true);
                }
            });
        }).start();
    }

    private void setControlsEnabled(boolean enabled) {
        btnApprove.setEnabled(enabled);
        btnCancel.setEnabled(enabled);
        btnBack.setEnabled(enabled);

        float alpha = enabled ? 1f : 0.6f;
        btnApprove.setAlpha(alpha);
        btnCancel.setAlpha(alpha);
        btnBack.setAlpha(alpha);
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
