package com.example.tlotlotau.Documents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;

import java.io.File;

public class DocumentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DOCUMENT_TYPE = "document_type";
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_CUSTOMER_NAME = "customer_name";
    public static final String EXTRA_TOTAL_AMOUNT = "total_amount";

    private TextView  tvCustomerName, tvTotalAmount;
    private Button btnShare, btnEdit, btnConvertOrPaid;
    private String documentType;
    private String filePath;
    private String customerName;
    private double totalAmount;
    private ImageButton btnBack;

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
        if (filePath != null) {
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
            Toast.makeText(this, "File path is null", Toast.LENGTH_SHORT).show();
        }

        displayDocumentDetails();
        setupButtonListeners();
    }

    private void initializeViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnShare = findViewById(R.id.btnShare);
        btnEdit = findViewById(R.id.btnEdit);
        btnConvertOrPaid = findViewById(R.id.btnConvertOrPaid);
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        documentType = intent.getStringExtra(EXTRA_DOCUMENT_TYPE);
        filePath = intent.getStringExtra(EXTRA_FILE_PATH);
        customerName = intent.getStringExtra(EXTRA_CUSTOMER_NAME);
        totalAmount = intent.getDoubleExtra(EXTRA_TOTAL_AMOUNT, 0.0);
    }

    private void displayDocumentDetails() {
        tvCustomerName.setText("Customer: " + customerName);
        tvTotalAmount.setText("Total Amount: R" + String.format("%.2f", totalAmount));
    }

    private void setupButtonListeners() {
        btnShare.setOnClickListener(v -> shareDocument());
        btnEdit.setOnClickListener(v -> editDocument());

        if ("invoice".equalsIgnoreCase(documentType)) {
            btnConvertOrPaid.setText("Mark as Paid");
            btnConvertOrPaid.setOnClickListener(v -> convertEstimateToInvoice());
        } else if ("estimate".equalsIgnoreCase(documentType)) {
            btnConvertOrPaid.setText("Covert to Invoice");
            btnConvertOrPaid.setOnClickListener(v -> markInvoiceAsPaid());
        } else {
            btnConvertOrPaid.setVisibility(View.GONE);
        }
    }

    private void shareDocument() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        // It's better to use FileProvider to get a content:// URI rather than using Uri.parse(filePath)
        // if the file is a local file. Ensure that your FileProvider is configured.
        Uri fileUri = Uri.parse(filePath);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Please see this " + documentType + ": " + filePath);
        startActivity(Intent.createChooser(shareIntent, "Share Document via"));
    }

    private void editDocument() {
        Intent intent;
        if ("invoice".equalsIgnoreCase(documentType)) {
            intent = new Intent(this, InvoicePreviewActivity.class);
        } else {
            intent = new Intent(this, EstimatePreviewActivity.class);
        }
        intent.putExtra(EXTRA_CUSTOMER_NAME, customerName);
        intent.putExtra(EXTRA_TOTAL_AMOUNT, totalAmount);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        startActivity(intent);
    }

    private void convertEstimateToInvoice() {
        // Implement conversion logic here
        Toast.makeText(this, "Estimate converted to invoice", Toast.LENGTH_SHORT).show();
    }

    private void markInvoiceAsPaid() {
        // Implement payment marking logic here
        Toast.makeText(this, "Invoice marked as paid", Toast.LENGTH_SHORT).show();
    }
}
