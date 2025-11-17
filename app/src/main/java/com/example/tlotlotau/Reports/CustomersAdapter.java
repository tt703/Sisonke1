package com.example.tlotlotau.Reports;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.VH> {
    private final List<Customer> items;
    public CustomersAdapter(List<Customer> items){ this.items = items; }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_row, parent, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH holder, int position){
        Customer c = items.get(position);
        holder.name.setText(c.getName());
        holder.phone.setText(c.getPhone());
        holder.amount.setText(String.format("R %s", c.getAmountDue() == null ? "0.00" : c.getAmountDue()));
    }
    @Override public int getItemCount(){ return items == null ? 0 : items.size(); }
    static class VH extends RecyclerView.ViewHolder {
        TextView name, phone, amount;
        VH(@NonNull View itemView){ super(itemView);
            name = itemView.findViewById(R.id.tvCustomerName);
            phone = itemView.findViewById(R.id.tvCustomerPhone);
            amount = itemView.findViewById(R.id.tvCustomerAmount);
        }
    }
}