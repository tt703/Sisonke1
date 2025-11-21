package com.example.tlotlotau.Reports;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.Main.HomeActivity;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Reports.ReportsPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;

public class ReportsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ReportsPagerAdapter adapter;
    private ImageButton btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnback = findViewById(R.id.btnBack);
        btnback.setOnClickListener(v -> finish());


        adapter = new ReportsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Customers"); break;
                case 1: tab.setText("Products"); break;
                case 2: tab.setText("Sales"); break;
                case 3: tab.setText("Invoices"); break;
                case 4: tab.setText("Estimates"); break;
                default: tab.setText("Tab " + position);
            }
        }).attach();
    }
}
