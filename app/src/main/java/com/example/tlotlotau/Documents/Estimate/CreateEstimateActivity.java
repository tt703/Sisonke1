package com.example.tlotlotau.Documents.Estimate;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tlotlotau.Customer.CreateCustomerFragment;
import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Customer.CustomerPagerAdapter;
import com.example.tlotlotau.Customer.SelectCustomerFragment;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Documents.Invoice.InvoiceViewModel;
import com.example.tlotlotau.Documents.Item;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Documents.Invoice.AddItemDialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class CreateEstimateActivity extends AppCompatActivity
        implements CreateCustomerFragment.OnCustomerCreatedListener, SelectCustomerFragment.OnCustomerSelectedListener {

    private InvoiceViewModel viewModel;
    private Customer selectedCustomer = null;
    private ArrayList<Customer> customerList = new ArrayList<>();
    private LinearLayout itemsContainer;
    private ViewGroup createLayout;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private ArrayList<Item> items;
    private TextView tvTitle;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // reuse exact invoice layout so UI is identical
        setContentView(R.layout.create_invoice);

        // wire basic views (IDs same as create_invoice.xml)
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.titleText);
        if (tvTitle != null) tvTitle.setText("Create Estimate");

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        itemsContainer = findViewById(R.id.itemsContainer);
        createLayout = findViewById(R.id.createInvoiceLayout);

        // customer pager setup (same as invoice)
        CustomerPagerAdapter pagerAdapter = new CustomerPagerAdapter(this, customerList);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Add Customer");
                    else tab.setText("Select Customer");
                }).attach();

        // populate customers
        DatabaseHelper db = new DatabaseHelper(this);
        customerList.clear();
        customerList.addAll(db.getAllCustomers());
        CustomerPagerAdapter pagerAdapter1 = new CustomerPagerAdapter(this, customerList);
        viewPager.setAdapter(pagerAdapter1);

        // restore any passed intent data (items / customer)
        retrieveIntentData();

        // reuse the InvoiceViewModel (keeps same behaviour as invoice)
        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        // Add / Preview buttons from same layout
        Button addItemButton = findViewById(R.id.btnAddItem);
        Button previewButton = findViewById(R.id.btnPreviewInvoice);

        // Add Item -> open product picker dialog (same as CreateInvoiceActivity)
        addItemButton.setOnClickListener(v -> {
            AddItemDialogFragment dlg = AddItemDialogFragment.newInstance();
            // set listener so dialog returns selected items
            dlg.setListener(selectedItems -> {
                // called on UI thread; append each selected product as an Item row
                for (Item it : selectedItems) {
                    addItemToContainer(itemsContainer, it);
                }
            });
            dlg.show(getSupportFragmentManager(), "add_item");
        });

        // Preview -> validate and open estimate preview activity
        previewButton.setOnClickListener(v -> {
            if (!validateCustomerDetails()) return;

            ArrayList<Item> items = extractItems(itemsContainer);

            if (!items.isEmpty()) {
                Customer useCustomer = selectedCustomer;

                // store into viewModel just like invoice does
                viewModel.setCustomer(useCustomer);
                viewModel.getItems().setValue(items);

                Intent previewIntent = new Intent(CreateEstimateActivity.this, EstimatePreviewActivity.class);
                previewIntent.putExtra("customer", (Parcelable) useCustomer);
                previewIntent.putParcelableArrayListExtra("items", items);
                startActivity(previewIntent);
            } else {
                displaySnackbar("Please add at least one item");
            }
        });

        // restore items from ViewModel if present
        ArrayList<Item> savedItems = viewModel.getItems().getValue();
        if (savedItems != null) {
            for (Item item : savedItems) {
                addItemToContainer(itemsContainer, item);
            }
        }
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            items = new ArrayList<>();
            selectedCustomer = null;
            return;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            items = new ArrayList<>();
            selectedCustomer = null;
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            items = extras.getParcelableArrayList("items", Item.class);
            selectedCustomer = extras.getParcelable("customer", Customer.class);
        } else {
            items = intent.getParcelableArrayListExtra("items");
            selectedCustomer = intent.getParcelableExtra("customer");
        }

        if (items == null) items = new ArrayList<>();

        if (selectedCustomer != null && viewPager != null) {
            customerList.add(selectedCustomer);
            viewPager.post(() -> viewPager.setCurrentItem(1, true));
        }

        Log.d(TAG, "CreateEstimate: received selectedCustomer = " + (selectedCustomer != null ? selectedCustomer.getName() : "null"));
    }

    private boolean ensureCustomerSelected() {
        if (selectedCustomer == null) {
            displaySnackbar("Please add or select a customer first");
            return false;
        }
        return true;
    }

    // --- reuse the same helper methods as in CreateInvoiceActivity ---
    private void addItemRow(LinearLayout itemsContainer) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        itemLayout.setPadding(pad, pad, pad, pad);
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
        itemPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        itemPrice.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        itemLayout.addView(itemPrice);

        EditText itemQuantity = new EditText(this);
        itemQuantity.setHint("Quantity");
        itemQuantity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
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
        return ensureCustomerSelected();
    }

    private ArrayList<Item> extractItems(LinearLayout itemsContainer) {
        ArrayList<Item> out = new ArrayList<>();

        for (int i = 0; i < itemsContainer.getChildCount(); i++) {
            ViewGroup itemLayout = (ViewGroup) itemsContainer.getChildAt(i);

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
                    out.add(new Item(name, quantity, price));
                } catch (NumberFormatException e) {
                    displaySnackbar("Invalid price or quantity");
                }
            }
        }
        return out;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore selectedCustomer from ViewModel if present
        Customer vmCustomer = viewModel.getCustomer().getValue();
        if (vmCustomer != null) {
            selectedCustomer = vmCustomer;
            if (viewPager != null) viewPager.setCurrentItem(1, true);
        }
        // restore items if any
        ArrayList<Item> saved = viewModel.getItems().getValue();
        if (saved != null && itemsContainer != null && itemsContainer.getChildCount() == 0) {
            for (Item it : saved) addItemToContainer(itemsContainer, it);
        }
    }

    @Override
    public void onCustomerCreated(@NonNull Customer customer) {
        customerList.add(customer);
        selectedCustomer = customer;
        if (viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyItemChanged(1);
        }
    }

    @Override
    public void onCustomerSelected(@NonNull Customer customer) {
        selectedCustomer = customer;
    }

    private void displaySnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.createInvoiceLayout), message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        snackbar.show();
    }
}
