package com.example.tlotlotau.Sales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    private LinearLayout itemsContainer;
    private TextView tvSubtotal, tvTax, tvTotal, tvPaymentMethod, tvReceivedChange, tvReceiptMeta;
    private ImageButton btnBack;
    private Button btnShare, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        itemsContainer = findViewById(R.id.itemsContainer);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvReceivedChange = findViewById(R.id.tvReceivedChange);
        tvReceiptMeta = findViewById(R.id.tvReceiptMeta);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShareReceipt);
        btnDone = findViewById(R.id.btnDone);

        // meta
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        tvReceiptMeta.setText(String.format("Date: %s", date));

        // read extras
        Intent intent = getIntent();
        String itemsText = intent.getStringExtra("items_text"); // preferred format
        String paymentMethod = intent.getStringExtra("payment_method");
        double subtotal = intent.getDoubleExtra("subtotal", Double.NaN);
        double tax = intent.getDoubleExtra("tax", Double.NaN);
        double total = intent.getDoubleExtra("total", Double.NaN);
        double received = intent.getDoubleExtra("received", Double.NaN);
        double change = intent.getDoubleExtra("change", Double.NaN);

        boolean populated = false;

        // 1) If items_text provided, parse and render
        if (itemsText != null && !itemsText.trim().isEmpty()) {
            populateItemsFromText(itemsText);
            populated = true;
        }

        // 2) If not, try Cart (useful if caller didn't clear the cart)
        if (!populated) {
            List<SaleItem> items = Cart.get().getItems();
            if (items != null && !items.isEmpty()) {
                for (SaleItem it : items) {
                    addItemRow(it.product.getProductName(), it.quantity, it.lineTotal());
                }
                populated = true;
            }
        }

        // If still not populated, show placeholder
        if (!populated) {
            addEmptyRow("No items to display");
        }

        // totals fallback to Cart if missing
        if (Double.isNaN(subtotal)) subtotal = Cart.get().subtotal();
        if (Double.isNaN(tax)) tax = Cart.get().tax(15.0);
        if (Double.isNaN(total)) total = Cart.get().total(15.0);

        tvSubtotal.setText(String.format(Locale.getDefault(), "R %.2f", subtotal));
        tvTax.setText(String.format(Locale.getDefault(), "R %.2f", tax));
        tvTotal.setText(String.format(Locale.getDefault(), "R %.2f", total));

        tvPaymentMethod.setText("Payment: " + (paymentMethod == null ? "UNKNOWN" : paymentMethod));

        if (!Double.isNaN(received) && !Double.isNaN(change)) {
            tvReceivedChange.setText(String.format(Locale.getDefault(), "Received: R %.2f   Change: R %.2f", received, change));
            tvReceivedChange.setVisibility(View.VISIBLE);
        } else {
            tvReceivedChange.setVisibility(View.GONE);
        }

        // share builds a simple text representation
        btnShare.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Receipt\n");
            sb.append(tvReceiptMeta.getText()).append("\n\n");
            for (int i = 0; i < itemsContainer.getChildCount(); i++) {
                View row = itemsContainer.getChildAt(i);
                TextView name = row.findViewById(R.id.rowItemName);
                TextView qty = row.findViewById(R.id.rowItemQty);
                TextView price = row.findViewById(R.id.rowItemLineTotal);
                if (name != null) sb.append(String.format("%s x%s  %s\n", name.getText(), qty.getText(), price.getText()));
            }
            sb.append("\nSubtotal: ").append(tvSubtotal.getText()).append("\n");
            sb.append("Tax: ").append(tvTax.getText()).append("\n");
            sb.append("Total: ").append(tvTotal.getText()).append("\n");
            sb.append(tvPaymentMethod.getText()).append("\n");
            if (tvReceivedChange.getVisibility() == View.VISIBLE) sb.append(tvReceivedChange.getText()).append("\n");

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(share, "Share receipt"));
        });

        // Done/back should return to SellProductActivity
        View.OnClickListener finishAndReturn = v -> {
            Intent i = new Intent(ReceiptActivity.this, SellProductActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        };

        btnBack.setOnClickListener(finishAndReturn);
        btnDone.setOnClickListener(finishAndReturn);
    }

    private void populateItemsFromText(String itemsText) {
        String[] lines = itemsText.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.contains("|")) {
                String[] parts = line.split("\\|");
                String name = parts.length > 0 ? parts[0] : "";
                int qty = 1;
                try { qty = parts.length > 1 ? Integer.parseInt(parts[1].replaceAll("[^0-9]", "")) : 1; } catch (Exception ignored){}
                double ltotal = 0.0;
                try { ltotal = parts.length > 2 ? Double.parseDouble(parts[2].replaceAll("[^0-9\\.]", "")) : 0.0; } catch (Exception ignored){}
                addItemRow(name, qty, ltotal);
            } else {
                addItemRow(line, 1, 0.0);
            }
        }
    }

    private void addItemRow(String name, int qty, double lineTotal) {
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.item_receipt_row, null);
        TextView tvName = row.findViewById(R.id.rowItemName);
        TextView tvQty = row.findViewById(R.id.rowItemQty);
        TextView tvLine = row.findViewById(R.id.rowItemLineTotal);

        tvName.setText(name);
        tvQty.setText(String.valueOf(qty));
        tvLine.setText(String.format(Locale.getDefault(), "R %.2f", lineTotal));
        itemsContainer.addView(row);
    }

    private void addEmptyRow(String text) {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView t = new TextView(this);
        t.setText(text);
        t.setPadding(6, 8, 6, 8);
        row.addView(t);
        itemsContainer.addView(row);
    }
}
