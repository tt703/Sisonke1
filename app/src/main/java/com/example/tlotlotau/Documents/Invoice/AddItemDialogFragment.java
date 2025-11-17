package com.example.tlotlotau.Documents.Invoice;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Documents.Item;
import com.example.tlotlotau.Inventory.CategoryAdapter2;
import com.example.tlotlotau.Inventory.CategoryC;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.R;

import java.util.ArrayList;
import java.util.List;

public class AddItemDialogFragment extends DialogFragment {

    public interface Listener {
        void onProductsSelected(ArrayList<Item> selected);
    }

    private Listener listener;
    private DatabaseHelper db;
    private RecyclerView rvCategories, rvProducts;
    private SearchView svSearch;
    private TextView tvSelectedCount;
    private Button btnAdd;
    private ImageButton btnClose;

    private CategoryAdapter2 catAdapter;
    private ProductSelectionAdapter prodAdapter;

    private long selectedCategoryId = -1L;
    private List<Product> allProducts = new ArrayList<>();
    private List<CategoryC> categories = new ArrayList<>();

    public AddItemDialogFragment() {}

    public static AddItemDialogFragment newInstance() {
        return new AddItemDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        db = new DatabaseHelper(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        }
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_item, container, false);

        rvCategories = v.findViewById(R.id.rvDialogCategories);
        rvProducts = v.findViewById(R.id.rvDialogProducts);
        svSearch = v.findViewById(R.id.svDialogSearch);
        btnAdd = v.findViewById(R.id.btnDialogAdd);
        tvSelectedCount = v.findViewById(R.id.tvSelectedCount);
        btnClose = v.findViewById(R.id.btnDialogClose);

        // load categories from DB and add "All"
        categories.clear();
        List<CategoryC> fromDb = db.getAllCategories();
        if (fromDb != null) categories.addAll(fromDb);
        CategoryC all = new CategoryC(-1L, "All", String.valueOf(System.currentTimeMillis()));
        categories.add(0, all);

        // CategoryAdapter2 expects OnClick(CategoryC)
        catAdapter = new CategoryAdapter2(categories, c -> {
            // c can be null safety-guarded but CategoryAdapter2 always passes a CategoryC
            selectedCategoryId = (c == null) ? -1L : c.getId();
            // update adapter selected state using its API
            catAdapter.setSelectedCategoryId(selectedCategoryId);
            // re-filter product list
            filterProducts(svSearch.getQuery() == null ? "" : svSearch.getQuery().toString());
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(catAdapter);
        // ensure visual selection is 'All' by default
        catAdapter.setSelectedCategoryId(-1L);

        // products
        allProducts = db.getAllProducts();
        if (allProducts == null) allProducts = new ArrayList<>();
        prodAdapter = new ProductSelectionAdapter(allProducts, this::onSelectionChanged);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(prodAdapter);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { filterProducts(query); return true; }
            @Override public boolean onQueryTextChange(String newText) { filterProducts(newText); return true; }
        });

        btnAdd.setOnClickListener(v1 -> {
            ArrayList<Item> selected = prodAdapter.getSelectedItemsAsInvoiceItems();
            if (selected.isEmpty()) {
                // nothing selected — you could show a Toast/snackbar if you like
                return;
            }
            if (listener != null) listener.onProductsSelected(selected);
            dismiss();
        });

        btnClose.setOnClickListener(v12 -> dismiss());

        updateSelectedCount();

        return v;
    }

    private void filterProducts(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Product> out = new ArrayList<>();
        for (Product p : allProducts) {
            boolean matchesCategory = (selectedCategoryId == -1L) ||
                    (p.getCategoryCId() != null && p.getCategoryCId().longValue() == selectedCategoryId);
            boolean matchesSearch = q.isEmpty() || (p.getProductName() != null && p.getProductName().toLowerCase().contains(q));
            if (matchesCategory && matchesSearch) out.add(p);
        }
        prodAdapter.updateData(out);
    }

    private void onSelectionChanged() {
        updateSelectedCount();
    }

    private void updateSelectedCount() {
        int cnt = prodAdapter.getSelectedCount();
        tvSelectedCount.setText(cnt + " selected");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // allow hosting Activity to set listener explicitly
    public void setListener(Listener l) {
        listener = l;
    }
}
