package com.example.tlotlotau.Documents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tlotlotau.Documents.Estimate.CreateEstimateActivity;
import com.example.tlotlotau.Documents.Invoice.CreateInvoiceActivity;
import com.example.tlotlotau.Main.HomeActivity;
import com.example.tlotlotau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DocumentsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabCreate;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabCreate = findViewById(R.id.fabCreate);
        btnBack = findViewById(R.id.btnBack);

        // Set the adapter for the ViewPager2
        DocumentsPagerAdapter adapter = new DocumentsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach TabLayoutMediator to link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Invoices");
                } else if (position == 1) {
                    tab.setText("Estimates");
                }
            }
        }).attach();

        btnBack.setOnClickListener(v-> startActivity(new Intent(DocumentsActivity.this, HomeActivity.class)));

        // Floating action button creates a new document based on current tab
        fabCreate.setOnClickListener(v -> {
            int position = viewPager.getCurrentItem();
            if (position == 0) {
                startActivity(new Intent(DocumentsActivity.this, CreateInvoiceActivity.class));
            } else if (position == 1) {
                startActivity(new Intent(DocumentsActivity.this, CreateEstimateActivity.class));
            }
        });
    }
}
