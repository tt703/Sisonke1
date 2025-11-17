package com.example.tlotlotau.Reports;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Sales.SaleRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesReportFragment extends Fragment {

    private DatabaseHelper db;
    private BarChart barChart;
    private PieChart pieChart;
    private RecyclerView recyclerSales;
    private TextView tvSalesTotal, tvSalesTax, tvSalesCount;

    public SalesReportFragment() { /* empty */ }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales_report, container, false);
        db = new DatabaseHelper(requireContext());

        barChart = v.findViewById(R.id.barChart);
        pieChart = v.findViewById(R.id.pieChart);
        recyclerSales = v.findViewById(R.id.recyclerSales);
        tvSalesTotal = v.findViewById(R.id.tvSalesTotal);
        tvSalesTax = v.findViewById(R.id.tvSalesTax);
        tvSalesCount = v.findViewById(R.id.tvSalesCount);

        recyclerSales.setLayoutManager(new LinearLayoutManager(requireContext()));


        loadSales();
        return v;
    }

    private void loadSales() {
        List<SaleRecord> sales = db.getAllSales();
        // totals
        double subtotal = 0;
        double tax = 0;
        for (SaleRecord s : sales) {
            subtotal += s.getSubtotal();
            tax += s.getTax();
        }
        double total = subtotal + tax;
        tvSalesTotal.setText(String.format("Total: R %.2f", total));
        tvSalesTax.setText(String.format("Tax: R %.2f", tax));
        tvSalesCount.setText(String.format("%d sales", sales.size()));

        // recycler
        SalesAdapter adapter = new SalesAdapter(sales);
        recyclerSales.setAdapter(adapter);

        // bar chart: show last N sales totals
        ArrayList<BarEntry> entries = new ArrayList<>();
        int idx = 0;
        // take up to last 10 for clarity
        int start = Math.max(0, sales.size() - 10);
        for (int i = start; i < sales.size(); i++) {
            entries.add(new BarEntry(idx++, (float) sales.get(i).getTotal()));
        }
        BarDataSet ds = new BarDataSet(entries, "Sales (last)");
        ds.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData bd = new BarData(ds);
        barChart.setData(bd);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        // pie chart: payment method distribution
        Map<String, Integer> counts = new HashMap<>();
        for (SaleRecord s : sales) counts.put(s.getPaymentMethod(),
                counts.getOrDefault(s.getPaymentMethod(), 0) + 1);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()) pieEntries.add(new PieEntry(e.getValue(), e.getKey()));
        PieDataSet pds = new PieDataSet(pieEntries, "Payment methods");
        pds.setColors(ColorTemplate.MATERIAL_COLORS);
        pieChart.setData(new PieData(pds));
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}
