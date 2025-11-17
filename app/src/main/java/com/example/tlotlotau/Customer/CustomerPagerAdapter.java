package com.example.tlotlotau.Customer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
public class CustomerPagerAdapter extends FragmentStateAdapter {
    private final List<Customer> customers;

    public CustomerPagerAdapter(@NonNull FragmentActivity fa, List<Customer> customers) {
        super(fa);
        this.customers = customers;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return CreateCustomerFragment.newInstance();
        } else {
            return SelectCustomerFragment.newInstance(customers);
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }



}
