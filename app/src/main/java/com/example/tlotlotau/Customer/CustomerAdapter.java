package com.example.tlotlotau.Customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;

import java.util.List;

public class CustomerAdapter extends  RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    public interface OnItemClickListener{
        void onItemClick( Customer customer);
    }
    private List<Customer>customers;
    private final OnItemClickListener listener;

    public CustomerAdapter(List<Customer>customers, OnItemClickListener listener){
        this.customers = customers;
        this.listener = listener;

    }
    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item,parent,false);
        return new CustomerViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position){
        Customer customer = customers.get(position);
        holder.bind(customer,listener);
    }
    @Override
    public int getItemCount(){
        return customers.size();
    }
    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private final TextView customerName;
        private final TextView amountDue;
        private final TextView numEstimatesSent;

        CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerName);
            amountDue = itemView.findViewById(R.id.amountDue);
            numEstimatesSent = itemView.findViewById(R.id.numEstimatesSent);

        }


        void bind(Customer customer, OnItemClickListener listener) {
            customerName.setText(customer.getName());
            amountDue.setText(customer.getAmountDue());
            numEstimatesSent.setText(customer.getNumEstimatesSent());
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(customer);
            });
        }


    }

}
