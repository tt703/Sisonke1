package com.example.tlotlotau.Documents.Estimate;

import static android.content.ContentValues.TAG;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Documents.Invoice.InvoiceViewModel;
import com.example.tlotlotau.Documents.Item;
import com.example.tlotlotau.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class CreateEstimateActivity extends AppCompatActivity {
    private InvoiceViewModel viewModel;
    private EditText customerNameField, customerAddressField, customerContactField;
    private Customer customer;
    private ArrayList<Item> items;
    private TextView tvTitle;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_estimate);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());


        // Initialize EditText fields
        customerNameField = findViewById(R.id.editCustomerName);
        customerAddressField = findViewById(R.id.editCustomerAddress);
        customerContactField = findViewById(R.id.editCustomerContact);



        retrieveIntentData();
        populateCustomerDetails();


        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        Button addItemButton = findViewById(R.id.btnAddItem);
        Button previewButton = findViewById(R.id.btnPreviewInvoice);
        LinearLayout itemsContainer = findViewById(R.id.itemsContainer);

        // Set up Add Item button listener
        addItemButton.setOnClickListener(v -> addItemRow(itemsContainer));

        // Set up Preview button listener
        previewButton.setOnClickListener(v -> {
            if (!validateCustomerDetails()) return;

            ArrayList<Item> items = extractItems(itemsContainer);

            if (!items.isEmpty()) {
                Customer customer = new Customer(
                        customerNameField.getText().toString().trim(),
                        customerAddressField.getText().toString().trim(),
                        customerContactField.getText().toString().trim()
                );

                viewModel.setCustomer(customer);
                viewModel.getItems().setValue(items);
                Intent previewIntent = new Intent(CreateEstimateActivity.this, EstimatePreviewActivity.class);
                previewIntent.putExtra("customer", (Parcelable) customer);
                previewIntent.putParcelableArrayListExtra("items", items);
                startActivity(previewIntent);
            } else {
                displaySnackbar("Please add at least one item");
            }
        });

        // Restore items if they already exist in the ViewModel
        ArrayList<Item> savedItems = viewModel.getItems().getValue();
        if (savedItems != null) {
            for (Item item : savedItems) {
                addItemToContainer(itemsContainer, item);
            }
        }
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                items = extras.getParcelableArrayList("items", Item.class);
                customer = extras.getParcelable("customer", Customer.class);
            } else {
                items = extras.getParcelableArrayList("items");
                customer = extras.getParcelable("customer");
            }

        } else {
            items = new ArrayList<>();
            customer = new Customer("", "", "");
        }
    }

    private void populateCustomerDetails() {
        if (customer != null) {
            customerNameField.setText(customer.getName());
            customerAddressField.setText(customer.getAddress());
            customerContactField.setText(customer.getContactInfo());
        }

        LinearLayout itemsContainer = findViewById(R.id.itemsContainer);
        for (Item item : items) {
            addItemToContainer(itemsContainer, item);
        }
    }

    private void addItemRow(LinearLayout itemsContainer) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        itemLayout.setPadding(8, 8, 8, 8);
        itemLayout.setBackgroundResource(R.drawable.item_row_background);

        EditText itemName = new EditText(this);
        itemName.setHint("Item Name");
        itemName.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        itemLayout.addView(itemName);

        EditText itemPrice = new EditText(this);
        itemPrice.setHint("Price");
        itemPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        itemPrice.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        itemLayout.addView(itemPrice);

        EditText itemQuantity = new EditText(this);
        itemQuantity.setHint("Quantity");
        itemQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        itemQuantity.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        itemLayout.addView(itemQuantity);

        ImageButton removeButton = new ImageButton(this);
        removeButton.setImageResource(android.R.drawable.ic_menu_delete);
        removeButton.setBackgroundColor(Color.TRANSPARENT);
        removeButton.setOnClickListener(removeItemView -> itemsContainer.removeView(itemLayout));
        itemLayout.addView(removeButton);

        itemsContainer.addView(itemLayout);
    }

    private void addItemToContainer(LinearLayout itemsContainer, Item item) {
        addItemRow(itemsContainer);
        LinearLayout itemLayout = (LinearLayout) itemsContainer.getChildAt(itemsContainer.getChildCount() - 1);

        EditText itemName = (EditText) itemLayout.getChildAt(0);
        EditText itemPrice = (EditText) itemLayout.getChildAt(1);
        EditText itemQuantity = (EditText) itemLayout.getChildAt(2);

        itemName.setText(item.getName());
        itemPrice.setText(String.valueOf(item.getPrice()));
        itemQuantity.setText(String.valueOf(item.getQuantity()));
    }

    private boolean validateCustomerDetails() {
        if (customerNameField.getText().toString().trim().isEmpty() ||
                customerAddressField.getText().toString().trim().isEmpty() ||
                customerContactField.getText().toString().trim().isEmpty()) {
            displaySnackbar("Please fill in all customer details");
            return false;
        }
        return true;
    }

    private ArrayList<Item> extractItems(LinearLayout itemsContainer) {
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < itemsContainer.getChildCount(); i++) {
            LinearLayout itemLayout = (LinearLayout) itemsContainer.getChildAt(i);

            EditText itemName = (EditText) itemLayout.getChildAt(0);
            EditText itemPrice = (EditText) itemLayout.getChildAt(1);
            EditText itemQuantity = (EditText) itemLayout.getChildAt(2);

            String name = itemName.getText().toString().trim();
            String priceText = itemPrice.getText().toString().trim();
            String quantityText = itemQuantity.getText().toString().trim();

            if (!name.isEmpty() && !priceText.isEmpty() && !quantityText.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceText);
                    int quantity = Integer.parseInt(quantityText);
                    items.add(new Item(name, quantity, price));
                } catch (NumberFormatException e) {
                    displaySnackbar("Invalid price or quantity");
                }
            }
        }

        return items;
    }
    private String formatItemDetails(ArrayList<Item> items) {
        StringBuilder itemDetails = new StringBuilder();
        for (Item item : items) {
            itemDetails.append(item.getName()).append(" - ").append(item.getQuantity()).append(" @ ").append(item.getPrice()).append("\n");
        }
        return itemDetails.toString();
    }
    private double calculateTotalAmount(ArrayList<Item> items) {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
    private void saveEstimateDetails(File pdfFile) {
        String customerName = customer.getName();
        String customerAddress = customer.getAddress();
        String customerContact = customer.getContactInfo();
        String itemDetails = formatItemDetails(items);
        double totalAmount = calculateTotalAmount(items);
        String filePath = pdfFile.getAbsolutePath();
        Log.d(TAG, "saveEstimateDetails: started");

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            boolean isInserted = dbHelper.insertEstimate(customerName, customerAddress, customerContact, itemDetails, totalAmount, filePath);
            if (isInserted) {
                Toast.makeText(this, "Estimate saved successfully.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "saveEstimateDetails: invoice saved successfully");
            } else {
                Toast.makeText(this, "Failed to save estimate.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "saveInvoiceDetails: failed to save invoice");
            }
        } catch (Exception e) {
            Log.e(TAG, "saveEstimateDetails: failed to save estimate", e);
            Toast.makeText(this, "Failed to save estimate.", Toast.LENGTH_SHORT).show();
        }
    }


    private void displaySnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.createEstimateLayout), message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }

    public static class Estimate {
        private int id;
        private String customerName;
        private String customerAddress;
        private String customerContact;
        private String itemDetails;
        private double totalAmount;
        private String timestamp;
        private String status;
        private String filePath; // Add this line

        @Override
        public String toString() {
            return "Invoice{" +
                    "id=" + id +
                    ", customerName='" + customerName + '\'' +
                    ", customerAddress='" + customerAddress + '\'' +
                    ", customerContact='" + customerContact + '\'' +
                    ", itemDetails='" + itemDetails + '\'' +
                    ", totalAmount=" + totalAmount +
                    ", filePath='" + filePath + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    '}';

        }

        public int getId() {return id;}

        public void setId(int id) {this.id = id;}

        public String getCustomerName() {return customerName;}

        public void setCustomerName(String customerName) {this.customerName = customerName;}

        public void setCustomerAddress(String customerAddress) {this.customerAddress = customerAddress;}

        public void setItemDetails(String itemDetails) {this.itemDetails = itemDetails;}

        public double getTotalAmount() {return totalAmount;}
        public void setTotalAmount(double totalAmount) {this.totalAmount = totalAmount;}
        public String getFilePath() {return filePath;}

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public void setCustomerContact(String customerContact) {
            this.customerContact = customerContact;
        }
    }
}