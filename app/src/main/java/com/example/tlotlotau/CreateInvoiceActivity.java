package com.example.tlotlotau;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class CreateInvoiceActivity extends AppCompatActivity {
    private InvoiceViewModel viewModel;
    private EditText customerNameField, customerAddressField, customerContactField;
    private Customer customer;
    private ArrayList<Item> items;
    private TextView tvTitle;
    private ViewGroup createInvoiceLayout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_invoice);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());


        // Initialize EditText fields
        customerNameField = findViewById(R.id.editCustomerName);
        customerAddressField = findViewById(R.id.editCustomerAddress);
        customerContactField = findViewById(R.id.editCustomerContact);

        createInvoiceLayout = findViewById(R.id.createInvoiceLayout);



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

                Intent previewIntent = new Intent(CreateInvoiceActivity.this, InvoicePreviewActivity.class);
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
            String actionType = extras.getString("actionType", "Create Invoice");
            Log.d(TAG, "retrieveIntentData: actionType = " + actionType);
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

        EditText itemName = (EditText) itemLayout.getChildAt(0); // No TextInputLayout
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

            EditText itemName = (EditText) itemLayout.getChildAt(0); // No TextInputLayout
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



    private void displaySnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.createInvoiceLayout), message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }
}