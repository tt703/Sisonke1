package com.example.tlotlotau.Sales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.R;
import java.util.ArrayList;
import java.util.List;

public class SalesProductAdapter extends RecyclerView.Adapter<SalesProductAdapter.VH>{
    public interface Listener { void onAdd(Product p);}
    private final List<Product> items;
    final Listener listener;

    public SalesProductAdapter(List<Product> items, Listener listener){
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    // allow updating list without re-creating adapter
    public void updateData(List<Product> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_sale, parent, false);
        return new VH(v);
    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position){
        Product p = items.get(position);
        holder.name.setText(p.getProductName());
        holder.price.setText(String.format("R%.2f", p.getProductPrice()));
        holder.stock.setText("Stock: " + p.getProductQuantity());
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAdd(p);
        });
    }
    @Override public int getItemCount() {return items == null ? 0 : items.size();}
    static class VH extends RecyclerView.ViewHolder{
        TextView name, price, stock;
        ImageButton btnAdd;
        VH(@NonNull View itemView){
            super(itemView);
            name =  itemView.findViewById(R.id.tvProductName);
            price = itemView.findViewById(R.id.tvProductPrice);
            stock = itemView.findViewById(R.id.tvProductQuantity);
            btnAdd = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
