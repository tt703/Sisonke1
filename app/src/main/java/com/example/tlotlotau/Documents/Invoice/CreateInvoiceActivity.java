package com.example.tlotlotau.Documents.Invoice;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Customer.CustomerPagerAdapter;
import com.example.tlotlotau.Customer.CreateCustomerFragment;
import com.example.tlotlotau.Customer.SelectCustomerFragment;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Documents.Item;
import com.example.tlotlotau.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class CreateInvoiceActivity extends AppCompatActivity
        implements CreateCustomerFragment.OnCustomerCreatedListener, SelectCustomerFragment.OnCustomerSelectedListener{
    private InvoiceViewModel viewModel;
    private LinearLayout itemsContainer;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private final ArrayList<Customer> customerList = new ArrayList<>();
    private ArrayList<Item> items;
    private Customer customer,useCustomer;
    private Customer selectedCustomer = null;
    private ViewGroup createInvoiceLayout;
    private ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_invoice);
        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);



        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        retrieveIntentData();


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        createInvoiceLayout = findViewById(R.id.createInvoiceLayout);

        CustomerPagerAdapter pagerAdapter = new CustomerPagerAdapter(this, customerList);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position)-> {
                      if (position == 0) tab.setText("Add Customer");
                      else tab.setText("Select Customer");
                }).attach();

        DatabaseHelper db = new DatabaseHelper(this);
        customerList.clear();
        customerList.addAll(db.getAllCustomers());
        CustomerPagerAdapter pagerAdapter1 = new CustomerPagerAdapter(this, customerList);
        viewPager.setAdapter(pagerAdapter1);
        
        itemsContainer = findViewById(R.id.itemsContainer);

        Button addItemButton = findViewById(R.id.btnAddItem);
        Button previewButton = findViewById(R.id.btnPreviewInvoice);


        // Set up Add Item button listener
        // Set up Add Item button -> open dialog
        addItemButton.setOnClickListener(v -> {
            AddItemDialogFragment dlg = AddItemDialogFragment.newInstance();
            // use activity as listener (CreateInvoiceActivity implements the listener)
            dlg.setListener(selectedItems -> {
                // called on UI thread
                for (Item it : selectedItems) {
                    addItemToContainer(itemsContainer, it);
                }
            });
            dlg.show(getSupportFragmentManager(), "add_item");
        });


        // Set up Preview button listener
        previewButton.setOnClickListener(v -> {
            if (!validateCustomerDetails()) return;

            ArrayList<Item> items = extractItems(itemsContainer);

            if (!items.isEmpty()) {
                Customer useCustomer = selectedCustomer;

                viewModel.setCustomer(useCustomer);
                viewModel.getItems().setValue(items);
                Intent previewIntent = new Intent(CreateInvoiceActivity.this, InvoicePreviewActivity.class);
                previewIntent.putExtra("customer", useCustomer);
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
        if (intent == null) return;

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
        Log.d(TAG, "InvoicePreview: received customer = " + (customer != null ? customer.getName() : "null"));
        Log.d(TAG, "CreateInvoice: onCreate received selectedCustomer = " + (selectedCustomer != null ? selectedCustomer.getName() : "null"));

    }


    private boolean ensureCustomerSelected(){
        if (selectedCustomer == null){
            displaySnackbar("Please add or select a customer first");
            return false;
        }
        return true;
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


    private boolean validateCustomerDetails(){
        return ensureCustomerSelected();

    }
    @Override
    protected void onResume() {
        super.onResume();
        // restore selectedCustomer from ViewModel if present
        Customer vmCustomer = viewModel.getCustomer().getValue();
        if (vmCustomer != null) {
            selectedCustomer = vmCustomer;
            // update any UI if needed (e.g. move ViewPager to select tab)
            if (viewPager != null) viewPager.setCurrentItem(1, true);
        }
        // restore items if any
        ArrayList<Item> saved = viewModel.getItems().getValue();
        if (saved != null && itemsContainer != null && itemsContainer.getChildCount() == 0) {
            for (Item it : saved) addItemToContainer(itemsContainer, it);
        }
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

    @Override
    public void onCustomerCreated(@NonNull Customer customer) {
        customerList.add(customer);
        selectedCustomer = customer;
        if (viewPager.getAdapter() != null){
            viewPager.getAdapter().notifyItemChanged(1);
        }

    }
    @Override
    public void onCustomerSelected(@NonNull Customer customer) {
        selectedCustomer = customer;
    }

}

