package com.example.tlotlotau.Reports;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tlotlotau.Documents.Invoice.Invoice;
import com.example.tlotlotau.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.VH> {
    private final List<Invoice> items;
    public InvoiceAdapter(List<Invoice> items){ this.items = items; }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_row, parent, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH holder, int position){
        Invoice i = items.get(position);
        holder.customer.setText(i.getCustomerName());
        holder.total.setText(String.format("R %.2f", i.getTotalAmount()));
        holder.date.setText(i.getTimestamp());
    }
    @Override public int getItemCount(){ return items == null ? 0 : items.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView customer, total, date;
        VH(@NonNull View itemView){
            super(itemView);
            customer = itemView.findViewById(R.id.tvInvoiceCustomer);
            total = itemView.findViewById(R.id.tvInvoiceTotal);
            date = itemView.findViewById(R.id.tvInvoiceDate);
        }
    }
}
