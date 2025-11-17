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
import com.example.tlotlotau.Documents.Invoice.Invoice;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Reports.InvoiceAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoicesReportFragment extends Fragment {
    private DatabaseHelper db;
    private PieChart pieChart;
    private RecyclerView recycler;
    private TextView tvCount, tvTotal;

    public InvoicesReportFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invoices_report, container, false);
        db = new DatabaseHelper(requireContext());
        pieChart = v.findViewById(R.id.pieChartInvoices);
        recycler = v.findViewById(R.id.recyclerInvoices);
        tvCount = v.findViewById(R.id.tvInvoicesCount);
        tvTotal = v.findViewById(R.id.tvInvoicesTotal);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadInvoices();
        return v;
    }

    private void loadInvoices() {
        List<Invoice> items = db.getAllInvoices();
        double total = 0;
        for (Invoice i : items) total += i.getTotalAmount();
        tvCount.setText(String.format("%d invoices", items.size()));
        tvTotal.setText(String.format("Total: R %.2f", total));

        InvoiceAdapter adapter = new InvoiceAdapter(items);
        recycler.setAdapter(adapter);

        Map<String,Integer> byMonth = new HashMap<>();
        for (Invoice i : items) {
            String ts = i.getTimestamp() == null ? "unknown" : i.getTimestamp().substring(0, Math.min(10, i.getTimestamp().length()));
            byMonth.put(ts, byMonth.getOrDefault(ts, 0) + 1);
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String,Integer> e : byMonth.entrySet()) entries.add(new PieEntry(e.getValue(), e.getKey()));
        PieDataSet ds = new PieDataSet(entries, "Invoices by day");
        ds.setColors(ColorTemplate.MATERIAL_COLORS);
        pieChart.setData(new PieData(ds));
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}
