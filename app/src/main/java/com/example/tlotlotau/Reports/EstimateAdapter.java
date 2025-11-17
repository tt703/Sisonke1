package com.example.tlotlotau.Reports;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tlotlotau.Documents.Estimate.Estimate;
import com.example.tlotlotau.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EstimateAdapter extends RecyclerView.Adapter<EstimateAdapter.VH> {
    private final List<Estimate> items;
    public EstimateAdapter(List<Estimate> items){ this.items = items; }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estimate_row, parent, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH holder, int position){
        Estimate e = items.get(position);
        holder.name.setText(e.getCustomerName());
        holder.total.setText(String.format("R %.2f", e.getTotalAmount()));
        holder.date.setText(e.getTimestamp());
    }
    @Override public int getItemCount(){ return items == null ? 0 : items.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView name, total, date;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvEstimateCustomer);
            total = itemView.findViewById(R.id.tvEstimateTotal);
            date = itemView.findViewById(R.id.tvEstimateDate);
        }
    }
}
