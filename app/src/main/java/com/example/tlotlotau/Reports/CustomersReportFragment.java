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
import com.example.tlotlotau.R;
import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Reports.CustomersAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomersReportFragment extends Fragment {

    private DatabaseHelper db;
    private PieChart pieChart;
    private RecyclerView recycler;
    private TextView tvCount, tvTotalDue;

    public CustomersReportFragment() { }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customers_report, container, false);
        db = new DatabaseHelper(requireContext());
        pieChart = v.findViewById(R.id.pieChartCustomers);
        recycler = v.findViewById(R.id.recyclerCustomers);
        tvCount = v.findViewById(R.id.tvCustomersCount);
        tvTotalDue = v.findViewById(R.id.tvCustomersTotalDue);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadCustomers();
        return v;
    }

    private void loadCustomers() {
        List<Customer> customers = db.getAllCustomers();
        int count = customers.size();
        double totalDue = 0;
        Map<String, Integer> buckets = new HashMap<>(); // bucket by >0 due vs 0
        buckets.put("With Due", 0);
        buckets.put("No Due", 0);
        for (Customer c : customers) {
            double due = 0;
            try { due = Double.parseDouble(String.valueOf(c.getAmountDue())); } catch (Exception ignored) {}
            totalDue += due;
            if (due > 0) buckets.put("With Due", buckets.get("With Due") + 1);
            else buckets.put("No Due", buckets.get("No Due") + 1);
        }

        tvCount.setText(String.format("%d customers", count));
        tvTotalDue.setText(String.format("Total due: R %.2f", totalDue));

        // recycler
        CustomersAdapter adapter = new CustomersAdapter(customers);
        recycler.setAdapter(adapter);

        // pie
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String,Integer> e : buckets.entrySet()) {
            entries.add(new PieEntry(e.getValue(), e.getKey()));
        }
        PieDataSet ds = new PieDataSet(entries, "Customer status");
        ds.setColors(ColorTemplate.MATERIAL_COLORS);
        pieChart.setData(new PieData(ds));
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}