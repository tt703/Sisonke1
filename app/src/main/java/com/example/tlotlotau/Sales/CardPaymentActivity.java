package com.example.tlotlotau.Sales;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class CardPaymentActivity extends AppCompatActivity {

    // --- Constants ---
    private static final String TAG = "CardPaymentActivity";
    private static final double TAX_PERCENT = 15.0;
    // ⚠️ SECURITY WARNING: Move this to a secure server for production apps
    private static final String YOCO_SECRET_KEY = "sk_test_7a9b4a92nm3zKl49900409889891";
    private static final String YOCO_API_URL = "https://payments.yoco.com/api/checkouts";

    // --- UI Messages ---
    private static final String MSG_CONNECTING = "Connecting to Yoco...";
    private static final String MSG_APPROVED = "Payment Approved! Redirecting...";
    private static final String MSG_DECLINED = "Payment Declined.";
    private static final String MSG_ERROR_DB = "Database Error: Sale not saved.";
    private static final String BTN_RETRY = "Retry";

    // --- UI Components ---
    private TextView tvAmount;
    private MaterialButton btnApprove;
    private MaterialButton btnCancel;
    private ImageButton btnBack;
    private ProgressBar pbWaiting;
    private TextView tvStatus;
    private ImageView ivStatusIcon;

    private final DecimalFormat money = new DecimalFormat("R #,##0.00");

    // --- RESULT HANDLER ---
    private final ActivityResultLauncher<Intent> yocoPaymentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // 🟢 1. SUCCESS
                    handlePaymentSuccess();
                } else {
                    // 🔴 2. FAIL
                    handlePaymentFailure();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        initializeViews();

        // 1. Show Amount
        double amount = Cart.get().total(TAX_PERCENT);
        tvAmount.setText(money.format(amount));

        // 2. Set Initial Button Text
        btnApprove.setText("Pay " + money.format(amount));

        // --- LISTENERS ---
        btnBack.setOnClickListener(v -> finish());

        btnCancel.setOnClickListener(v -> {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnApprove.setOnClickListener(v -> {
            if (Cart.get().isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate Cents (R100.00 -> 10000 cents)
            long amountInCents = (long) (Cart.get().total(TAX_PERCENT) * 100);

            // Start the process
            initiateYocoPayment(amountInCents);
        });
    }

    private void initializeViews() {
        tvAmount = findViewById(R.id.tvCardAmount);
        btnApprove = findViewById(R.id.btnCardApprove);
        btnCancel = findViewById(R.id.btnCardCancel);
        btnBack = findViewById(R.id.btnBack);
        pbWaiting = findViewById(R.id.pbWaiting);
        tvStatus = findViewById(R.id.tvStatus);
        ivStatusIcon = findViewById(R.id.ivStatusIcon);
    }

    // =========================================================
    // STEP 1: FETCH URL
    // =========================================================
    private void initiateYocoPayment(long amountInCents) {
        setLoadingState(true, MSG_CONNECTING);

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(YOCO_API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + YOCO_SECRET_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(10000); // 10 second timeout
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);

                // Create JSON Body
                String jsonBody = createCheckoutBody(amountInCents);

                // Send Data
                try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                    writer.write(jsonBody);
                    writer.flush();
                }

                // Handle Response
                int responseCode = conn.getResponseCode();
                if (responseCode == 201 || responseCode == 200) {
                    // Success: Read Stream
                    StringBuilder content = new StringBuilder();
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) content.append(inputLine);
                    }

                    JSONObject response = new JSONObject(content.toString());
                    String redirectUrl = response.optString("redirectUrl");

                    if (!redirectUrl.isEmpty()) {
                        runOnUiThread(() -> openYocoWebView(redirectUrl));
                    } else {
                        runOnUiThread(() -> handleApiError("Error: No URL received from Yoco"));
                    }

                } else {
                    // Error: Read Error Stream if possible
                    runOnUiThread(() -> handleApiError("Server Error: " + responseCode));
                }

            } catch (Exception e) {
                Log.e(TAG, "Network Error", e);
                runOnUiThread(() -> handleApiError("Connection Error. Check Internet."));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private String createCheckoutBody(long amountInCents) {
        try {
            JSONObject json = new JSONObject();
            json.put("amount", amountInCents);
            json.put("currency", "ZAR");
            json.put("successUrl", "https://demo.app/success");
            json.put("cancelUrl", "https://demo.app/cancel");
            json.put("failureUrl", "https://demo.app/fail");
            return json.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    private void openYocoWebView(String url) {
        // Safe check to ensure activity is still alive
        if (isFinishing() || isDestroyed()) return;

        pbWaiting.setVisibility(View.GONE);
        Intent intent = new Intent(this, YocoPaymentActivity.class);
        intent.putExtra("PAYMENT_URL", url);
        yocoPaymentLauncher.launch(intent);
    }

    // =========================================================
    // STEP 2: HANDLE SUCCESS -> REDIRECT
    // =========================================================
    private void handlePaymentSuccess() {
        // Update UI
        tvStatus.setText(MSG_APPROVED);
        ivStatusIcon.setImageResource(R.drawable.ic_check_circle);
        pbWaiting.setVisibility(View.VISIBLE);

        btnApprove.setEnabled(false);
        btnCancel.setEnabled(false);

        // Save to DB in background
        new Thread(() -> {
            boolean success = false;
            String method = "CARD";
            DatabaseHelper db = new DatabaseHelper(CardPaymentActivity.this);

            try {
                // Gather Data
                List<SaleItem> items = Cart.get().getItems();
                double subtotal = Cart.get().subtotal();
                double tax = Cart.get().tax(TAX_PERCENT);
                double total = Cart.get().total(TAX_PERCENT);

                SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                String userId = sp.getString("user_id", "0");
                String userName = sp.getString("user_name", "Guest");
                String userRole = sp.getString("user_role", "Staff");

                success = db.createSale(userId, userName, userRole, items, subtotal, tax, total, method);
            } catch (Exception ex) {
                Log.e(TAG, "Database Error", ex);
            }

            final boolean finalSuccess = success;

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;

                pbWaiting.setVisibility(View.GONE);
                if (finalSuccess) {
                    goToReceipt(method);
                } else {
                    handleApiError(MSG_ERROR_DB);
                }
            });
        }).start();
    }

    private void goToReceipt(String method) {
        Intent i = new Intent(CardPaymentActivity.this, ReceiptActivity.class);
        i.putExtra("payment_method", method);
        i.putExtra("subtotal", Cart.get().subtotal());
        i.putExtra("tax", Cart.get().tax(TAX_PERCENT));
        i.putExtra("total", Cart.get().total(TAX_PERCENT));
        i.putExtra("items_text", buildItemsTextForIntent());

        startActivity(i);
        Cart.get().clear();
        finish();
    }

    // =========================================================
    // STEP 3: HANDLE FAILURE -> RETRY
    // =========================================================
    private void handlePaymentFailure() {
        setLoadingState(false, MSG_DECLINED);
        ivStatusIcon.setImageResource(R.drawable.ic_close);
        btnApprove.setText(BTN_RETRY);
    }

    private void handleApiError(String errorMsg) {
        // Safe check
        if (isFinishing() || isDestroyed()) return;

        setLoadingState(false, errorMsg);
        ivStatusIcon.setImageResource(R.drawable.ic_close);
        btnApprove.setText(BTN_RETRY);
    }

    private void setLoadingState(boolean isLoading, String statusText) {
        if (isFinishing() || isDestroyed()) return;

        tvStatus.setText(statusText);

        if (isLoading) {
            btnApprove.setEnabled(false);
            btnCancel.setEnabled(false);
            btnBack.setEnabled(false);
            btnApprove.setAlpha(0.6f);
            pbWaiting.setVisibility(View.VISIBLE);
        } else {
            btnApprove.setEnabled(true);
            btnCancel.setEnabled(true);
            btnBack.setEnabled(true);
            btnApprove.setAlpha(1.0f);
            pbWaiting.setVisibility(View.GONE);
        }
    }

    private String buildItemsTextForIntent() {
        StringBuilder sb = new StringBuilder();
        for (SaleItem it : Cart.get().getItems()) {
            sb.append(it.product.getProductName().replace("|", " ")).append("|")
                    .append(it.quantity).append("|")
                    .append(String.format(Locale.getDefault(), "%.2f", it.lineTotal()));
            sb.append("\n");
        }
        return sb.toString();
    }
}