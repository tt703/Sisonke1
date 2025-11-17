package com.example.tlotlotau.Reports;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReportsPagerAdapter extends FragmentStateAdapter {
    public ReportsPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new CustomersReportFragment();
            case 1: return new ProductsReportFragment();
            case 2: return new SalesReportFragment();
            case 3: return new InvoicesReportFragment();
            case 4: return new EstimatesReportFragment();
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
