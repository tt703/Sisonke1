package com.example.tlotlotau.Reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Sales.SaleRecord;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.VH> {
    private final List<SaleRecord> items;
    public SalesAdapter(List<SaleRecord> items){ this.items = items;}
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_row, parent, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH holder, int position){
        SaleRecord s = items.get(position);
        holder.user.setText(s.getUserName() == null ? "Unknown" : s.getUserName());
        holder.time.setText(s.getTimestamp());
        holder.total.setText(String.format("R %.2f", s.getTotal()));
        holder.method.setText(s.getPaymentMethod());
    }
    @Override public int getItemCount(){ return items == null ? 0 : items.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView user, time, total, method;
        VH(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.tvSaleUser);
            time = itemView.findViewById(R.id.tvSaleTime);
            total = itemView.findViewById(R.id.tvSaleTotal);
            method = itemView.findViewById(R.id.tvSaleMethod);
        }
    }
}
