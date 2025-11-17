package com.example.tlotlotau.Documents.Invoice;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.Documents.Item;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductSelectionAdapter extends RecyclerView.Adapter<ProductSelectionAdapter.Holder> {

    private final List<Product> data = new ArrayList<>();
    // selected quantities
    private final Map<Integer, Integer> selected = new HashMap<>();
    private final Runnable selectionChangedCallback;

    public ProductSelectionAdapter(List<Product> initial, Runnable selectionChangedCallback) {
        if (initial != null) data.addAll(initial);
        this.selectionChangedCallback = selectionChangedCallback;
    }

    public void updateData(List<Product> newData) {
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_select, parent, false);
        return new Holder(v);
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        Product p = data.get(pos);
        h.name.setText(p.getProductName());
        h.price.setText(String.format("R %.2f", p.getProductPrice()));

        int qty = selected.containsKey(p.getProductId()) ? selected.get(p.getProductId()) : 0;
        if (qty <= 0) qty = 0; // not selected
        h.qty.setText(String.valueOf(Math.max(1, qty == 0 ? 1 : qty)));

        // plus
        h.btnInc.setOnClickListener(v -> {
            int cur = selected.containsKey(p.getProductId()) ? selected.get(p.getProductId()) : 0;
            cur++;
            // cap by product stock if desired:
            if (p.getProductQuantity() > 0 && cur > p.getProductQuantity()) cur = p.getProductQuantity();
            selected.put(p.getProductId(), cur);
            notifyItemChanged(pos);
            selectionChangedCallback.run();
        });
        // minus
        h.btnDec.setOnClickListener(v -> {
            int cur = selected.containsKey(p.getProductId()) ? selected.get(p.getProductId()) : 0;
            if (cur <= 0) {
                // if not selected, set 1->0 (toggle)
                h.qty.setText("1");
                return;
            }
            cur--;
            if (cur <= 0) selected.remove(p.getProductId()); else selected.put(p.getProductId(), cur);
            notifyItemChanged(pos);
            selectionChangedCallback.run();
        });

        // clicking row toggles selection (set qty 1 if not set)
        h.itemView.setOnClickListener(v -> {
            int cur = selected.containsKey(p.getProductId()) ? selected.get(p.getProductId()) : 0;
            if (cur <= 0) selected.put(p.getProductId(), 1);
            else selected.remove(p.getProductId());
            notifyItemChanged(pos);
            selectionChangedCallback.run();
        });

        // highlight selected
        boolean isSelected = selected.containsKey(p.getProductId()) && selected.get(p.getProductId()) > 0;
        h.itemView.setAlpha(isSelected ? 1f : 0.9f);
    }

    @Override public int getItemCount() { return data.size(); }

    public int getSelectedCount() { return selected.size(); }


    public ArrayList<Item> getSelectedItemsAsInvoiceItems() {
        ArrayList<Item> out = new ArrayList<>();
        for (Product p : data) {
            Integer q = selected.get(p.getProductId());
            if (q != null && q > 0) {
                // Item(name, qty, price)
                out.add(new Item(p.getProductName(), q, p.getProductPrice()));
            }
        }
        return out;
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, price, qty;
        ImageButton btnInc, btnDec;
        Holder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.tvProdName);
            price = v.findViewById(R.id.tvProdPrice);
            qty = v.findViewById(R.id.tvQty);
            btnInc = v.findViewById(R.id.btnInc);
            btnDec = v.findViewById(R.id.btnDec);
        }
    }
}
