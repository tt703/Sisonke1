package com.example.tlotlotau.Sales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlotlotau.R;
import java.util.List;
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH>{
    public interface Listener {
        void onIncrease(int productId);
        void onDecrease(int productId);
        void onRemove(int productId);
    }

    private final Listener listener;

    public CartAdapter(List<SaleItem> items, Listener listener){
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position){
        List<SaleItem> items = Cart.get().getItems();
        if (position < 0 || position >= items.size()) return;
        SaleItem item = items.get(position);
        holder.name.setText(item.product.getProductName());
        holder.qty.setText(String.valueOf(item.quantity));
        holder.lineTotal.setText(String.format("R%.2f", item.lineTotal()));
        holder.btnPlus.setOnClickListener(v -> listener.onIncrease(item.product.getProductId()));
        holder.btnMinus.setOnClickListener(v -> listener.onDecrease(item.product.getProductId()));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(item.product.getProductId()));
    }

    @Override public int getItemCount() {
        return Cart.get().getItems().size();
    }

    static class VH extends RecyclerView.ViewHolder{
        TextView name, qty, lineTotal;
        ImageButton btnPlus, btnMinus, btnRemove;
        VH(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.tvCartName);
            qty = itemView.findViewById(R.id.tvCartQty);
            lineTotal = itemView.findViewById(R.id.tvCartLineTotal);
            btnPlus = itemView.findViewById(R.id.btnCartPlus);
            btnMinus = itemView.findViewById(R.id.btnCartMinus);
            btnRemove = itemView.findViewById(R.id.btnCartRemove);
        }
    }
}
