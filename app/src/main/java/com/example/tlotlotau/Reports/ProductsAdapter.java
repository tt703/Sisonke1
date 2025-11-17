package com.example.tlotlotau.Reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.VH> {
    private final List<Product> items;
    public ProductsAdapter(List<Product> items){ this.items = items; }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_report, parent, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH holder, int position){
        Product p = items.get(position);
        holder.name.setText(p.getProductName());
        holder.qty.setText(String.valueOf(p.getProductQuantity()));
        holder.price.setText(String.format("R %.2f", p.getProductPrice()));
    }
    @Override public int getItemCount(){ return items == null ? 0 : items.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView name, qty, price;
        VH(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.tvProdName);
            qty = itemView.findViewById(R.id.tvProdQty);
            price = itemView.findViewById(R.id.tvProdPrice);
        }
    }
}
