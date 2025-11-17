package com.example.tlotlotau.Customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;

import java.util.List;

/**
 * A RecyclerView adapter for displaying a list of Customer objects.
 * This adapter is designed to be efficient and reusable.
 */
public class CustomerAdapter2 extends RecyclerView.Adapter<CustomerAdapter2.VH> {

    public interface OnClick {
        void onClick(Customer c);
    }

    private final List<Customer> items;
    private final OnClick clickListener;


    public CustomerAdapter2(List<Customer> items, OnClick clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single customer item.
        // This assumes you have a layout file named 'customer_item2.xml' in your res/layout folder.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item2, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        // Get the customer data for the current position.
        final Customer customer = items.get(position);

        // Safely set the customer's name, defaulting to an empty string if null.
        String name = (customer.getName() != null) ? customer.getName() : "";
        holder.tvName.setText(name);

        // Determine the contact info, preferring email over phone.
        String contact = "";
        // Using a try-catch block is safe but checking for nulls is often cleaner.
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            contact = customer.getEmail();
        } else if (customer.getPhone() != null) {
            contact = customer.getPhone();
        }
        holder.tvContact.setText(contact);

        // Set the click listener for the entire item view.
        holder.itemView.setOnClickListener(v -> {
            // Ensure the listener is not null before invoking the callback.
            if (clickListener != null) {
                clickListener.onClick(customer);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list, or 0 if the list is null.
        return items != null ? items.size() : 0;
    }


    static class VH extends RecyclerView.ViewHolder {
        // Declare the views that will be populated.
        final TextView tvName;
        final TextView tvContact;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvContact = itemView.findViewById(R.id.tvCustomerContact);


            if (tvName == null || tvContact == null) {
                throw new IllegalStateException("One or more views not found in customer_item2.xml. " +
                        "Check that tvCustomerName and tvCustomerContact IDs exist.");
            }
        }
    }
}
