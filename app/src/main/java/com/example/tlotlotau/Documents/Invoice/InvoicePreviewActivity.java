package com.example.tlotlotau.Documents.Invoice;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Documents.DocumentsActivity;
import com.example.tlotlotau.Documents.Item;
import com.example.tlotlotau.Settings.EditCompanyInfoActivity;
import com.example.tlotlotau.Main.MainActivity;
import com.example.tlotlotau.R;
import com.example.tlotlotau.databinding.InvoicePreviewBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvoicePreviewActivity extends AppCompatActivity {

    private InvoicePreviewBinding binding;
    private ArrayList<Item> items;
    private Customer customer;
    private ExecutorService executorService;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = InvoicePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->{

                }
        );


        executorService = Executors.newSingleThreadExecutor();

        binding.previewInvoiceButton.setOnClickListener(v -> {
            executorService.execute(() -> {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InvoicePreviewActivity.this);
                    MaterialCardView customLayout = (MaterialCardView) getLayoutInflater().inflate(R.layout.custome_alert_dialog, null);
                    builder.setView(customLayout);

                    AlertDialog alertDialog = builder.create();

                    TextView title = customLayout.findViewById(R.id.alertTitle);
                    TextView message = customLayout.findViewById(R.id.alertMessage);
                    Button positiveButton = customLayout.findViewById(R.id.positiveButton);
                    Button negativeButton = customLayout.findViewById(R.id.negativeButton);

                    title.setText("Preview Invoice PDF");
                    message.setText("You have requested to preview the invoice PDF. Please note that if you proceed, the invoice will be saved and you will not be able to edit it.");

                    positiveButton.setOnClickListener(v1 -> {
                        alertDialog.dismiss();
                        executorService.execute(() -> {
                            File pdfFile = createInvoicePDF();
                            runOnUiThread(() -> {
                                if (pdfFile != null) {
                                    saveInvoiceDetails(pdfFile);
                                    previewInvoice(pdfFile);
                                } else {
                                    Toast.makeText(InvoicePreviewActivity.this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    });

                    negativeButton.setOnClickListener(v12 -> alertDialog.dismiss());

                    alertDialog.show();
                });
            });
        });
        initializeUI();
        retrieveIntentData();
        populateCompanyDetails();
        populateCustomerDetails();
        populateInvoiceTable();
        setupButtonListeners();
        setupBottomNavigation();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(InvoicePreviewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Intent intent = new Intent(InvoicePreviewActivity.this, DocumentsActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void saveInvoiceDetails(File pdfFile) {
        String customerName = customer.getName();
        String customerAddress = customer.getAddress();
        String customerContact = customer.getContactInfo();
        String itemDetails = formatItemDetails(items);
        double totalAmount = calculateTotalAmount(items);
        String filePath = pdfFile.getAbsolutePath();
        Log.d(TAG, "saveInvoiceDetails: started");

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            boolean isInserted = dbHelper.insertInvoice(customerName, customerAddress, customerContact, itemDetails, totalAmount, filePath);
            if (isInserted) {
                Toast.makeText(this, "Invoice saved successfully.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "saveInvoiceDetails: invoice saved successfully");
            } else {
                Toast.makeText(this, "Failed to save invoice.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "saveInvoiceDetails: failed to save invoice");
            }
        } catch (Exception e) {
            Log.e(TAG, "saveInvoiceDetails: failed to save invoice", e);
            Toast.makeText(this, "Failed to save invoice.", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatItemDetails(ArrayList<Item> items) {
        StringBuilder itemDetails = new StringBuilder();
        for (Item item : items) {
            itemDetails.append(item.getName())
                    .append(" - Quantity")
                    .append(item.getQuantity())
                    .append(" - Price:")
                    .append(item.getPrice())
                    .append("\n");
        }
        return itemDetails.toString();
    }

    private double calculateTotalAmount(ArrayList<Item> items) {
        double totalAmount = 0.0;
        for (Item item : items) {
            totalAmount += item.getQuantity() * item.getPrice();
        }
        return totalAmount;
    }

    private void initializeUI() {
        int buttonBackgroundColor = Color.parseColor("#FFD700"); // Gold
        int buttonTextColor = Color.parseColor("#FFFFFF"); // White

        binding.editInvoiceButton.setBackgroundColor(buttonBackgroundColor);
        binding.editInvoiceButton.setTextColor(buttonTextColor);

        binding.previewInvoiceButton.setBackgroundColor(buttonBackgroundColor);
        binding.previewInvoiceButton.setTextColor(buttonTextColor);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setSelectedItemId(R.id.nav_invoices);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(InvoicePreviewActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_invoices) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(InvoicePreviewActivity.this, EditCompanyInfoActivity.class));
                return true;
            }
            return false;
        });
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Log.d(TAG, "retrieveIntentData: started");
        if (extras != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                items = extras.getParcelableArrayList("items", Item.class);
                customer = extras.getParcelable("customer", Customer.class);
            } else {
                items = extras.getParcelableArrayList("items");
                customer = extras.getParcelable("customer");
            }
            String actionType = extras.getString("actionType", "Create Invoice");
            Log.d(TAG, "retrieveIntentData: actionType = " + actionType);
        } else {
            items = new ArrayList<>();
        }
        Log.d(TAG, "retrieveIntentData: items size = " + (items != null ? items.size() : "null"));
    }

    private void populateCompanyDetails() {
        SharedPreferences preferences = getSharedPreferences("CompanyInfo", MODE_PRIVATE);

        String companyName = preferences.getString("CompanyName", "");
        String companyAddress = preferences.getString("CompanyAddress", "");
        String vatNumber = preferences.getString("VATNumber", "");
        String regNumber = preferences.getString("RegistrationNumber", "");
        String bankName = preferences.getString("BankName", "");
        String accountNumber = preferences.getString("AccountNumber", "");
        String branchCode = preferences.getString("BranchCode", "");

        Log.d("InvoiceGenerator", "Company Details: " + companyName + ", " + companyAddress + ", " + vatNumber + ", " + regNumber);
        Log.d("InvoiceGenerator", "Bank Details: " + bankName + ", " + accountNumber + ", " + branchCode);

        binding.invoiceCompanyName.setText(preferences.getString("CompanyName", "LESKARATSHEPO"));
        binding.invoiceCompanyAddress.setText(preferences.getString("CompanyAddress", ""));
        binding.invoiceVATNumber.setText(preferences.getString("VATNumber", ""));
        binding.invoiceRegistrationNumber.setText(preferences.getString("RegistrationNumber", ""));
        binding.invoiceBankDetails.setText(String.format(Locale.getDefault(), "Bank: %s\nAcc: %s\nBranch: %s",
                preferences.getString("BankName", ""),
                preferences.getString("AccountNumber", ""),
                preferences.getString("BranchCode", "")));
    }

    private void populateCustomerDetails() {
        if (customer != null) {
            binding.invoiceBillTo.setText(String.format(Locale.getDefault(), "Bill To: %s\n%s\n%s",
                    customer.getName(),
                    customer.getAddress(),
                    customer.getContactInfo()));
        } else {
            binding.invoiceBillTo.setText(getString(R.string.bill_to_not_available));
        }
    }

    private void populateInvoiceTable() {
        double totalAmount = 0.0;

        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
                Log.d("InvoicePreview", "Item:" + item.getName() + ",Quantity:" + item.getQuantity() + ",Price:" + item.getPrice());
                totalAmount += item.getQuantity() * item.getPrice();

                // Create a new row for each item
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                // Item Name
                TextView itemName = new TextView(this);
                itemName.setText(item.getName());
                row.addView(itemName);
                // Item Quantity
                TextView itemQuantity = new TextView(this);
                itemQuantity.setText(String.valueOf(item.getQuantity()));
                row.addView(itemQuantity);
                // Item Price
                TextView itemPrice = new TextView(this);
                itemPrice.setText(String.format(Locale.getDefault(), "R%.2f", item.getPrice()));
                row.addView(itemPrice);
                // Add the row to the table
                binding.invoiceItemsTable.addView(row);
            }
        } else {
            Toast.makeText(this, "No items to display.", Toast.LENGTH_SHORT).show();
        }

        DecimalFormat df = new DecimalFormat("#.00");
        binding.invoiceTotal.setText(getString(R.string.total_amount_format, df.format(totalAmount)));
        binding.invoiceTotal.setTypeface(Typeface.DEFAULT_BOLD);
        binding.invoiceTotal.setTextColor(Color.parseColor("#000000"));
    }

    private void setupButtonListeners() {
        binding.editInvoiceButton.setOnClickListener(v -> navigateToCreateInvoice());
    }

    private void navigateToCreateInvoice() {
        Intent editIntent = new Intent(this, CreateInvoiceActivity.class);
        editIntent.putParcelableArrayListExtra("items", items);
        editIntent.putParcelableArrayListExtra("customer", customer);
        startActivity(editIntent);
    }


    private File createInvoicePDF() {

        SharedPreferences preferences = getSharedPreferences("CompanyInfo", MODE_PRIVATE);
        File pdfFile = new File(getExternalFilesDir(null), "Invoice.pdf");

        try {
            Rectangle customPageSize = new Rectangle(600f, 900f);
            Document document = new Document(customPageSize, 36f, 36f, 36f, 36f); // Margins
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            //Add Title to Invoice
            Paragraph title = new Paragraph("INVOICE", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(10);
            title.setSpacingAfter(10);
            document.add(title);

            // Retrieve and log company details
            String companyName = preferences.getString("CompanyName", "LESKARATSHEPO");
            String companyAddress = preferences.getString("CompanyAddress", "");
            String vatNumber = preferences.getString("VATNumber", "");
            String regNumber = preferences.getString("RegistrationNumber", "");
            Log.d("InvoiceGenerator", "Company Details: " + companyName + ", " + companyAddress + ", " + vatNumber + ", " + regNumber);

            // Header Table
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new int[]{1, 3});

            // Logo Cell
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            logoCell.setPadding(10);

            // Add logo to the PDF
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.company_logo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image logo = Image.getInstance(stream.toByteArray());
                logo.scaleToFit(100, 100);
                logoCell.addElement(logo);
                Log.d("InvoiceGenerator", "Logo added successfully to PDF");
            } catch (Exception e) {
                Log.e("InvoiceGenerator", "Logo not added to PDF", e);
            }
            headerTable.addCell(logoCell);

            // Company Details Cell
            PdfPCell companyDetailsCell = new PdfPCell();
            companyDetailsCell.setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            companyDetailsCell.setPadding(10);
            companyDetailsCell.addElement(new Phrase(companyName, headerFont));
            companyDetailsCell.addElement(new Phrase(companyAddress, contentFont));
            companyDetailsCell.addElement(new Phrase("VAT No: " + vatNumber, contentFont));
            companyDetailsCell.addElement(new Phrase("Reg. No: " + regNumber, contentFont));
            headerTable.addCell(companyDetailsCell);

            document.add(headerTable);

            // "Bill To" Section
            PdfPTable billToTable = new PdfPTable(1);
            billToTable.setWidthPercentage(100);
            billToTable.setSpacingBefore(25);
            PdfPCell billToHeader = new PdfPCell(new Phrase("Bill To:", subHeaderFont));
            billToHeader.setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            billToHeader.setPadding(10);
            billToTable.addCell(billToHeader);

            if (customer != null) {
                PdfPCell customerDetails = new PdfPCell();
                customerDetails.setPadding(10);
                customerDetails.addElement(new Phrase("Customer Name:  " + customer.getName(), contentFont));
                customerDetails.addElement(new Phrase("Address:  " + customer.getAddress(), contentFont));
                customerDetails.addElement(new Phrase("Contact No:  " + customer.getContactInfo(), contentFont));
                billToTable.addCell(customerDetails);
            }
            document.add(billToTable);

            // Items Table
            PdfPTable itemsTable = new PdfPTable(3);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{3, 1, 1});
            itemsTable.setSpacingBefore(20);

            // Table Header
            itemsTable.addCell(new PdfPCell(new Phrase("Item Description", subHeaderFont)) {{
                setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            }});
            itemsTable.addCell(new PdfPCell(new Phrase("Quantity", subHeaderFont)) {{
                setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            }});
            itemsTable.addCell(new PdfPCell(new Phrase("Price", subHeaderFont)) {{
                setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            }});

            // Table Content
            double totalAmount = 0.0;
            int rowsAdded = 0;
            for (Item item : items) {
                itemsTable.addCell(new PdfPCell(new Phrase(item.getName(), contentFont)));
                itemsTable.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), contentFont)));
                itemsTable.addCell(new PdfPCell(new Phrase(String.format(Locale.getDefault(), "R%.2f", item.getPrice()), contentFont)));
                totalAmount += item.getQuantity() * item.getPrice();
                rowsAdded++;
            }

            // Fill remaining rows to ensure table has 10 rows
            while (rowsAdded < 10) {
                itemsTable.addCell(new PdfPCell(new Phrase(" "))); // Blank cell
                itemsTable.addCell(new PdfPCell(new Phrase(" ")));
                itemsTable.addCell(new PdfPCell(new Phrase(" ")));
                rowsAdded++;
            }
            document.add(itemsTable);

            // Retrieve and log banking details
            String bankName = preferences.getString("BankName", "");
            String accountNumber = preferences.getString("AccountNumber", "");
            String branchCode = preferences.getString("BranchCode", "");
            Log.d("InvoiceGenerator", "Bank Details: " + bankName + ", " + accountNumber + ", " + branchCode);

            // Footer
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new int[]{2, 1});
            footerTable.setSpacingBefore(25);

            // Bank Details
            PdfPCell bankDetailsCell = new PdfPCell();
            bankDetailsCell.setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            bankDetailsCell.setPadding(10);
            bankDetailsCell.addElement(new Phrase("Bank Details:", subHeaderFont));
            bankDetailsCell.addElement(new Phrase("Bank: " + bankName, contentFont));
            bankDetailsCell.addElement(new Phrase("Account No: " + accountNumber, contentFont));
            bankDetailsCell.addElement(new Phrase("Branch Code: " + branchCode, contentFont));
            footerTable.addCell(bankDetailsCell);

            String invoiceID = generateInvoiceID();
            String currentDate = getCurrentDate();
            String dueDate = getDueDate();
            Log.d("InvoiceGenerator", "Invoice ID: " + invoiceID);
            Log.d("InvoiceGenerator", "Date of Print: " + currentDate);
            Log.d("InvoiceGenerator", "Due Date: " + dueDate);

            // Total and Additional Details
            PdfPCell totalCell = new PdfPCell();
            totalCell.setBackgroundColor(new BaseColor(211, 211, 211)); // Light gray
            totalCell.setPadding(10);
            totalCell.addElement(new Phrase(String.format(Locale.getDefault(), "Total: R%.2f", totalAmount), subHeaderFont));
            totalCell.addElement(new Phrase("Invoice ID: " + invoiceID, contentFont));
            totalCell.addElement(new Phrase("Date of Print: " + currentDate, contentFont));
            totalCell.addElement(new Phrase("Due Date: " + dueDate, contentFont));
            footerTable.addCell(totalCell);

            document.add(footerTable);

            // Thank-You Note
            Paragraph thankYou = new Paragraph("Thank you for your business!", contentFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            thankYou.setSpacingBefore(10);
            document.add(thankYou);

            document.close();

        } catch (Exception e) {
            Log.e("InvoiceGenerator", "Error generating PDF", e);
        }

        return pdfFile;
    }

    private String getDueDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dueDate = sdf.format(calendar.getTime());
        Log.d("InvoiceGenerator", "Due Date: " + dueDate);
        return dueDate;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        Log.d("InvoiceGenerator", "Current Date: " + currentDate);
        return currentDate;
    }

    private String generateInvoiceID() {
        return "INV" + (int) (Math.random() * 10000);
    }


    private void previewInvoice(File pdfFile) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }
}