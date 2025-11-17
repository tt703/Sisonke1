package com.example.tlotlotau.Documents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlotlotau.Documents.Estimate.EstimatePreviewActivity;
import com.example.tlotlotau.Documents.Invoice.InvoicePreviewActivity;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import java.io.File;

public class DocumentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DOCUMENT_TYPE = "document_type"; // "invoice" or "estimate"
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_CUSTOMER_NAME = "customer_name";
    public static final String EXTRA_TOTAL_AMOUNT = "total_amount";
    public static final String EXTRA_DOCUMENT_ID = "document_id"; // new: DB row id (long)

    private TextView tvCustomerName, tvTotalAmount;
    private Button btnShare,  btnConvertOrPaid;
    private String documentType;
    private String filePath;
    private String customerName;
    private double totalAmount;
    private ImageButton btnBack;
    private long documentId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        // Initialize back button first (independent of intent data)
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Retrieve intent data first so filePath and others are set before using them.
        retrieveIntentData();

        // Initialize views that display document details and buttons.
        initializeViews();

        // Set up the PDF RecyclerView only when filePath is available.
        if (filePath != null && !filePath.isEmpty()) {
            RecyclerView pdfRecyclerView = findViewById(R.id.pdfRecyclerView);
            pdfRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            File pdfFile = new File(filePath);
            PdfPageAdapter adapter = new PdfPageAdapter(this, pdfFile);
            pdfRecyclerView.setAdapter(adapter);

            // Verify that the file exists.
            if (!pdfFile.exists()) {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // No file available but still display metadata
        }

        displayDocumentDetails();
        setupButtonListeners();
    }

    private void initializeViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnShare = findViewById(R.id.btnShare);
        btnConvertOrPaid = findViewById(R.id.btnConvertOrPaid);
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;
        documentType = intent.getStringExtra(EXTRA_DOCUMENT_TYPE);
        filePath = intent.getStringExtra(EXTRA_FILE_PATH);
        customerName = intent.getStringExtra(EXTRA_CUSTOMER_NAME);
        totalAmount = intent.getDoubleExtra(EXTRA_TOTAL_AMOUNT, 0.0);
        documentId = intent.getLongExtra(EXTRA_DOCUMENT_ID, -1L);
    }

    private void displayDocumentDetails() {
        tvCustomerName.setText("Customer: " + (customerName == null ? "-" : customerName));
        tvTotalAmount.setText("Total Amount: R" + String.format("%.2f", totalAmount));
    }

    private void setupButtonListeners() {
        btnShare.setOnClickListener(v -> shareDocument());

        if ("invoice".equalsIgnoreCase(documentType)) {
            // For invoices we allow marking as paid (if an ID is provided)
            btnConvertOrPaid.setText("Mark as Paid");
            btnConvertOrPaid.setOnClickListener(v -> {
                if (documentId == -1L) {
                    Toast.makeText(this, "Invoice id not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                markInvoiceAsPaid(documentId);
            });
        } else if ("estimate".equalsIgnoreCase(documentType)) {
            // For estimates we allow converting -> invoice
            btnConvertOrPaid.setText("Convert to Invoice");
            btnConvertOrPaid.setOnClickListener(v -> {
                if (documentId == -1L) {
                    Toast.makeText(this, "Estimate id not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                convertEstimateToInvoice(documentId);
            });
        } else {
            btnConvertOrPaid.setVisibility(View.GONE);
        }
    }

    private void shareDocument() {
        if (filePath == null || filePath.isEmpty()) {
            Toast.makeText(this, "No file to share", Toast.LENGTH_SHORT).show();
            return;
        }
        File f = new File(filePath);
        if (!f.exists()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", f);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Please see this " + documentType + ": " + f.getName());
        startActivity(Intent.createChooser(shareIntent, "Share Document via"));
    }



    private void convertEstimateToInvoice(long estimateId) {
        DatabaseHelper db = new DatabaseHelper(this);
        long newInvoiceId = db.createInvoiceFromEstimate(estimateId);
        if (newInvoiceId != -1L) {
            Toast.makeText(this, "Estimate converted to invoice (id=" + newInvoiceId + ")", Toast.LENGTH_SHORT).show();
            // Open documents list or open the invoice preview — here we go back to DocumentsActivity
            Intent i = new Intent(this, com.example.tlotlotau.Documents.DocumentsActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Failed to convert estimate", Toast.LENGTH_SHORT).show();
        }
    }

    private void markInvoiceAsPaid(long invoiceId) {
        DatabaseHelper db = new DatabaseHelper(this);
        boolean ok = db.markInvoicePaid(invoiceId);
        if (ok) {
            Toast.makeText(this, "Invoice marked as paid", Toast.LENGTH_SHORT).show();
            btnConvertOrPaid.setEnabled(false);
            btnConvertOrPaid.setText("Paid");
        } else {
            Toast.makeText(this, "Failed to mark invoice as paid", Toast.LENGTH_SHORT).show();
        }
    }
}
