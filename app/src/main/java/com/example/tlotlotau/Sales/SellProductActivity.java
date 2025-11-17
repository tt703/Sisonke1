package com.example.tlotlotau.Sales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Inventory.CategoryC;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.Inventory.CategoryAdapter2;
import com.example.tlotlotau.R;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class SellProductActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView rvProducts, rvCategoriesSelect, rvCart;
    private SalesProductAdapter productAdapter;
    private CartAdapter cartAdapter;
    private TextView tvSubtotal, tvTax, tvTotal;
    private com.google.android.material.button.MaterialButton btnProceedPayment;
    private ImageButton btnBack;
    private com.google.android.material.button.MaterialButton btnScanQRCode;
    private TabLayout tabLayout;
    private View selectContainer, scanContainer;
    private SearchView svSearch;

    private List<Product> fullProducts = new ArrayList<>();
    private long selectedCategoryId = -1L;
    private static final double TAX_PERCENT = 15.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_products);

        db = new DatabaseHelper(this);


        tabLayout = findViewById(R.id.tabLayout);
        selectContainer = findViewById(R.id.selectContainer);
        scanContainer = findViewById(R.id.scanContainer);
        svSearch = findViewById(R.id.svSearch);

        rvProducts = findViewById(R.id.rvProducts);
        rvCategoriesSelect = findViewById(R.id.rvCategoriesSelect);
        rvCart = findViewById(R.id.rvCart);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);

        btnProceedPayment = findViewById(R.id.btnProceedPayment);
        btnBack = findViewById(R.id.btnBack);
        btnScanQRCode = findViewById(R.id.btnScanQRCode);

        // header buttons
        btnBack.setOnClickListener(v -> finish());

        // Tab setup
        tabLayout.addTab(tabLayout.newTab().setText("Select"));
        tabLayout.addTab(tabLayout.newTab().setText("Scan"));
        TabLayout.Tab first = tabLayout.getTabAt(0);
        if (first != null) {
            first.select();
            selectContainer.setVisibility(View.VISIBLE);
            scanContainer.setVisibility(View.GONE);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    selectContainer.setVisibility(View.VISIBLE);
                    scanContainer.setVisibility(View.GONE);
                } else {
                    selectContainer.setVisibility(View.GONE);
                    scanContainer.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // recyclers layout
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvCategoriesSelect.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setNestedScrollingEnabled(true);
        rvCart.setHasFixedSize(false);

        // load products and categories
        fullProducts = db.getAllProducts();
        if (fullProducts == null) fullProducts = new ArrayList<>();
        setupProductAdapter(fullProducts);

        // categories
        List<CategoryC> cats = db.getAllCategories();
        CategoryC all = new CategoryC(-1L, "All", String.valueOf(System.currentTimeMillis()));
        List<CategoryC> catsDisplay = new ArrayList<>();
        catsDisplay.add(all);
        if (cats != null) catsDisplay.addAll(cats);

        CategoryAdapter2 categoryAdapter = new CategoryAdapter2(catsDisplay, c -> {
            selectedCategoryId = (c == null) ? -1L : c.getId();
            applyFilters(svSearch.getQuery() == null ? "" : svSearch.getQuery().toString());
            RecyclerView.Adapter ad = rvCategoriesSelect.getAdapter();
            if (ad instanceof CategoryAdapter2) {
                ((CategoryAdapter2) ad).setSelectedCategoryId(selectedCategoryId);
            }
        });
        rvCategoriesSelect.setAdapter(categoryAdapter);
        categoryAdapter.setSelectedCategoryId(-1L);

        // cart adapter (initialise)
        cartAdapter = new CartAdapter(Cart.get().getItems(), new CartAdapter.Listener() {
            @Override public void onIncrease(int productId) {
                Product p = db.getProductById(productId);
                int current = Cart.get().getQuantityForProduct(productId);
                if (p != null && current + 1 > p.getProductQuantity()) {
                    Toast.makeText(SellProductActivity.this, "No more stock", Toast.LENGTH_SHORT).show();
                    return;
                }
                Cart.get().setQuantity(productId, current + 1);
                refreshAfterCartChange();
            }
            @Override public void onDecrease(int productId) {
                int current = Cart.get().getQuantityForProduct(productId);
                int newQ = Math.max(0, current - 1);
                Cart.get().setQuantity(productId, newQ);
                refreshAfterCartChange();
            }
            @Override public void onRemove(int productId) {
                Cart.get().removeProduct(productId);
                refreshAfterCartChange();
            }
        });
        rvCart.setAdapter(cartAdapter);

        // proceed button
        btnProceedPayment.setOnClickListener(v -> {
            if (Cart.get().isEmpty()) {
                Toast.makeText(this, "Cart empty", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, PaymentSelectionActivity.class));
        });

        // scan QR button
        btnScanQRCode.setOnClickListener(v -> startQRScan());

        // search
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { applyFilters(query); return true; }
            @Override public boolean onQueryTextChange(String newText) { applyFilters(newText); return true; }
        });

        // initial refresh
        refreshCart();
    }

    private void refreshAfterCartChange() {
        if (productAdapter != null) productAdapter.notifyDataSetChanged();
        if (cartAdapter != null) cartAdapter.notifyDataSetChanged();
        refreshCart();
    }

    private void setupProductAdapter(List<Product> products) {
        if (productAdapter == null) {
            productAdapter = new SalesProductAdapter(products, p -> {
                if (p.getProductQuantity() <= 0) {
                    Toast.makeText(this, "Out of stock", Toast.LENGTH_SHORT).show();
                    return;
                }
                Cart.get().addProduct(p, 1);
                refreshAfterCartChange();
            });
            rvProducts.setAdapter(productAdapter);
        } else {
            try { productAdapter.updateData(products); }
            catch (Exception e) {
                productAdapter = new SalesProductAdapter(products, productAdapter == null ? p -> {} : productAdapter.listener);
                rvProducts.setAdapter(productAdapter);
            }
        }
    }

    private void applyFilters(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Product> filtered = new ArrayList<>();
        for (Product p : fullProducts) {
            Long pCatObj = null;
            try { pCatObj = p == null ? null : p.getCategoryCId(); } catch (Exception ignored) {}
            boolean matchesCategory = (selectedCategoryId == -1L) || (pCatObj != null && pCatObj.longValue() == selectedCategoryId);
            boolean matchesSearch = q.isEmpty() || (p.getProductName() != null && p.getProductName().toLowerCase().contains(q));
            if (matchesCategory && matchesSearch) filtered.add(p);
        }
        setupProductAdapter(filtered);
    }

    private void startQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan product QR");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult res = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (res != null && res.getContents() != null) {
            String qr = res.getContents();
            Product p = db.getProductByQRCode(qr);
            if (p != null) {
                if (p.getProductQuantity() <= 0) {
                    Toast.makeText(this, "Product out of stock", Toast.LENGTH_SHORT).show();
                } else {
                    Cart.get().addProduct(p, 1);
                    refreshAfterCartChange();
                }
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void refreshCart() {
        // recalc and update totals
        double subtotal = Cart.get().subtotal();
        double tax = Cart.get().tax(TAX_PERCENT);
        double total = Cart.get().total(TAX_PERCENT);

        if (tvSubtotal != null) tvSubtotal.setText(String.format("R %.2f", subtotal));
        if (tvTax != null) tvTax.setText(String.format("R %.2f", tax));
        if (tvTotal != null) tvTotal.setText(String.format("R %.2f", total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullProducts = db.getAllProducts();
        if (fullProducts == null) fullProducts = new ArrayList<>();
        applyFilters(svSearch.getQuery() == null ? "" : svSearch.getQuery().toString());
        refreshCart();
    }
}
