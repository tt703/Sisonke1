package com.example.tlotlotau.Reports;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Reports.ProductsAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;

public class ProductsReportFragment extends Fragment {
    private DatabaseHelper db;
    private BarChart barChart;
    private RecyclerView recycler;
    private TextView tvCount, tvLowStock;

    public ProductsReportFragment() {}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products_report, container, false);
        db = new DatabaseHelper(requireContext());
        barChart = v.findViewById(R.id.barChartProducts);
        recycler = v.findViewById(R.id.recyclerProducts);
        tvCount = v.findViewById(R.id.tvProductsCount);
        tvLowStock = v.findViewById(R.id.tvProductsLowStock);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadProducts();
        return v;
    }

    private void loadProducts() {
        List<Product> products = db.getAllProducts();
        int count = products.size();
        int low = 0;
        for (Product p : products) if (p.getProductQuantity() <= 5) low++;

        tvCount.setText(String.format("%d products", count));
        tvLowStock.setText(String.format("%d low stock", low));

        ProductsAdapter adapter = new ProductsAdapter(products);
        recycler.setAdapter(adapter);

        ArrayList<BarEntry> entries = new ArrayList<>();
        int idx = 0;
        for (Product p : products) {
            entries.add(new BarEntry(idx++, (float) p.getProductQuantity()));
        }
        BarDataSet ds = new BarDataSet(entries, "Stock per product");
        ds.setColors(ColorTemplate.MATERIAL_COLORS);
        barChart.setData(new BarData(ds));
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }
}