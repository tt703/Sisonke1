package com.example.tlotlotau.Documents;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DocumentsPagerAdapter extends FragmentStateAdapter {

    public DocumentsPagerAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position){
        if (position == 0){
            return new InvoicesFragment();
        } else {
            return new EstimatesFragment();
        }
    }
    @Override
    public int getItemCount(){
        return 2;
    }
}
