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
import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Sales.SaleRecord;
import com.example.tlotlotau.Sales.SaleItem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesReportFragment extends Fragment {

    private DatabaseHelper db;
    private LineChart lineChart;
    private PieChart pieChart;
    private RecyclerView recyclerSales;
    private TextView tvSalesTotal, tvSalesTax, tvSalesCount, tvAvgOrder, tvTopProducts;

    public SalesReportFragment() { /* empty */ }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales_report, container, false);
        db = new DatabaseHelper(requireContext());

        lineChart = v.findViewById(R.id.lineChart);
        pieChart = v.findViewById(R.id.pieChart);
        recyclerSales = v.findViewById(R.id.recyclerSales);
        tvSalesTotal = v.findViewById(R.id.tvSalesTotal);
        tvSalesTax = v.findViewById(R.id.tvSalesTax);
        tvSalesCount = v.findViewById(R.id.tvSalesCount);
        tvAvgOrder = v.findViewById(R.id.tvAvgOrder);
        tvTopProducts = v.findViewById(R.id.tvTopProducts);

        recyclerSales.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadSales();
        return v;
    }

    private void loadSales() {
        List<SaleRecord> sales = db.getAllSales();
        if (sales == null) sales = new ArrayList<>();

        // totals
        double subtotal = 0.0;
        double tax = 0.0;
        for (SaleRecord s : sales) {
            if (s == null) continue;
            subtotal += safeDouble(s.getSubtotal());
            tax += safeDouble(s.getTax());
        }
        double total = subtotal + tax;
        tvSalesTotal.setText(String.format(Locale.getDefault(), "Total: R %.2f", total));
        tvSalesTax.setText(String.format(Locale.getDefault(), "Tax: R %.2f", tax));
        tvSalesCount.setText(String.format(Locale.getDefault(), "%d sales", sales.size()));

        // avg order
        double avg = (sales.isEmpty() ? 0.0 : total / sales.size());
        tvAvgOrder.setText(String.format(Locale.getDefault(), "R %.2f", avg));

        // recycler - recent sales
        SalesAdapter adapter = new SalesAdapter(sales);
        recyclerSales.setAdapter(adapter);

        // TREND LINE (sales totals over time) - use sales timestamp order (assuming db.getAllSales returns newest first)
        ArrayList<Entry> entries = new ArrayList<>();
        // Build in chronological order: older -> newer
        int n = sales.size();
        for (int i = 0; i < n; i++) {
            // pick chronological index
            SaleRecord s = sales.get(n - 1 - i); // reverse: last element is newest
            double val = safeDouble(s.getTotal());
            entries.add(new Entry(i, (float) val));
        }

        if (entries.isEmpty()) {
            lineChart.clear();
            lineChart.setNoDataText("No sales to display");
        } else {
            LineDataSet ds = new LineDataSet(entries, "Sales trend");
            ds.setMode(LineDataSet.Mode.LINEAR);
            ds.setDrawCircles(true);
            ds.setDrawValues(false);
            ds.setColors(ColorTemplate.MATERIAL_COLORS);
            ds.setLineWidth(2f);

            LineData ld = new LineData(ds);
            lineChart.setData(ld);
            if (lineChart.getDescription() != null) lineChart.getDescription().setEnabled(false);
            lineChart.invalidate();
        }

        // PIE CHART: payment method distribution
        Map<String, Integer> counts = new HashMap<>();
        for (SaleRecord s : sales) {
            String pm = (s == null || s.getPaymentMethod() == null) ? "Unknown" : s.getPaymentMethod();
            counts.put(pm, counts.getOrDefault(pm, 0) + 1);
        }
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            pieEntries.add(new PieEntry(e.getValue(), e.getKey()));
        }
        if (pieEntries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No payment data");
        } else {
            PieDataSet pds = new PieDataSet(pieEntries, "Payment methods");
            pds.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData pd = new PieData(pds);
            pd.setDrawValues(false);
            pieChart.setData(pd);
            if (pieChart.getDescription() != null) pieChart.getDescription().setEnabled(false);
            pieChart.invalidate();
        }

        // TOP PRODUCTS: aggregate across sale items
        Map<String, Integer> productCounts = new HashMap<>();
        Map<String, Double> productRevenue = new HashMap<>(); // optional revenue per product
        for (SaleRecord s : sales) {
            if (s == null) continue;
            List<SaleItem> items = db.getSaleItems(s.getId());
            if (items == null) continue;
            for (SaleItem it : items) {
                if (it == null || it.product == null) continue;
                String pname = it.product.getProductName() == null ? "Unknown" : it.product.getProductName();
                int qty = it.quantity;
                productCounts.put(pname, productCounts.getOrDefault(pname, 0) + qty);
                double rev = productRevenue.getOrDefault(pname, 0.0) + (it.product.getProductPrice() * qty);
                productRevenue.put(pname, rev);
            }
        }
        // pick top 3 products by quantity sold
        List<Map.Entry<String, Integer>> list = new ArrayList<>(productCounts.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        StringBuilder topSb = new StringBuilder();
        int limit = Math.min(3, list.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> e = list.get(i);
            String pname = e.getKey();
            int qty = e.getValue();
            double rev = productRevenue.getOrDefault(pname, 0.0);
            topSb.append(String.format(Locale.getDefault(), "%d. %s — %d sold (R %.2f)\n", i + 1, pname, qty, rev));
        }
        if (topSb.length() == 0) topSb.append("No products sold yet");
        tvTopProducts.setText(topSb.toString().trim());
    }

    private double safeDouble(Double v) {
        return v == null ? 0.0 : v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) {
            try { db.close(); } catch (Exception ignored) {}
            db = null;
        }
    }
}
