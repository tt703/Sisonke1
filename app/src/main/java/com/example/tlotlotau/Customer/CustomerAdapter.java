package com.example.tlotlotau.Customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Customer customer);
    }

    private final List<Customer> customers;
    private final OnItemClickListener listener;

    public CustomerAdapter(List<Customer> customers, OnItemClickListener listener) {
        // keep an internal mutable list
        this.customers = (customers == null) ? new ArrayList<>() : new ArrayList<>(customers);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer, listener);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    /**
     * Replace adapter data (useful after loading stats from DB)
     */
    public void updateData(List<Customer> newList) {
        customers.clear();
        if (newList != null && !newList.isEmpty()) customers.addAll(newList);
        notifyDataSetChanged();
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
            if (customer == null) return;

            customerName.setText(customer.getName() == null ? "" : customer.getName());

            // amountDue may be stored as string on customer; display R xx.xx
            String amt = customer.getAmountDue();
            if (amt == null || amt.trim().isEmpty()) amt = "0.00";
            amountDue.setText("R " + amt);

            String estimates = customer.getNumEstimatesSent();
            if (estimates == null || estimates.trim().isEmpty()) estimates = "0";
            numEstimatesSent.setText(estimates + " sent");

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(customer);
            });
        }
    }
}
