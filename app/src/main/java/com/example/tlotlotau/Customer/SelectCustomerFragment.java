package com.example.tlotlotau.Customer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tlotlotau.Employees.Category;
import com.example.tlotlotau.R;

import java.util.ArrayList;
import java.util.List;

public class SelectCustomerFragment extends Fragment {

    public interface OnCustomerSelectedListener {
        void onCustomerSelected(@NonNull Customer customer);
    }

    private static final String ARG_CUSTOMERS = "arg_customers";
    private OnCustomerSelectedListener listener;
    private List<Customer> customers = new ArrayList<>();

    public static SelectCustomerFragment newInstance(List<Customer> customers) {
        SelectCustomerFragment fragment = new SelectCustomerFragment();
        Bundle args = new Bundle();
        // ensure customers is ArrayList<Parcelable>
        args.putParcelableArrayList(ARG_CUSTOMERS, new ArrayList<>(customers));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCustomerSelectedListener) {
            listener = (OnCustomerSelectedListener) context;
        } else {
            throw new RuntimeException("Activity must implement OnCustomerSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_customer, container, false);

        if (getArguments() != null) {
            ArrayList<Customer> list = getArguments().getParcelableArrayList(ARG_CUSTOMERS);
            if (list != null) customers.addAll(list);
        }

        RecyclerView rv = v.findViewById(R.id.rvCustomers);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomerAdapter2 adapter2 = new CustomerAdapter2(customers, c -> {
            if (listener != null) listener.onCustomerSelected(c);
        });
        rv.setAdapter(adapter2);
        return v;
    }

}
