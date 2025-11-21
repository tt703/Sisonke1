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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Customers report with improved KPIs, aging buckets, top debtors and background DB loading.
 */
public class CustomersReportFragment extends Fragment {

    private DatabaseHelper db;
    private PieChart pieChart;
    private RecyclerView recycler;
    private TextView tvCount, tvTotalDue, tvOverdueCount, tvAvgInvoice, tvTopDebtor;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private DecimalFormat money = new DecimalFormat("R #,##0.00");

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
        tvOverdueCount = v.findViewById(R.id.tvCustomersOverdueCount);
        tvAvgInvoice = v.findViewById(R.id.tvAvgInvoiceValue);
        tvTopDebtor = v.findViewById(R.id.tvTopDebtor);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // load in background
        executor.execute(this::loadCustomersAndBuildReport);

        return v;
    }

    private void loadCustomersAndBuildReport() {
        try {
            // fetch raw data
            List<Customer> customers = db.getAllCustomers();
            List<com.example.tlotlotau.Documents.Invoice.Invoice> invoices = db.getAllInvoices();

            final int totalCustomers = customers.size();

            // totals and average invoice
            double totalDue = 0.0;
            double totalInvoiceValue = 0.0;
            int invoiceCount = 0;

            // aging buckets counts
            Map<String, Integer> aging = new HashMap<>();
            aging.put("0-30", 0);
            aging.put("31-60", 0);
            aging.put("61-90", 0);
            aging.put("90+", 0);

            // count customers with due vs no due
            int withDueCount = 0;
            int noDueCount = 0;

            // overdue threshold in days (e.g., overdue = >30 days)
            final long now = System.currentTimeMillis();
            final long DAY_MS = 24L * 60L * 60L * 1000L;

            // Map customerName -> sumDue and lastInvoiceDate
            Map<String, Double> customerDueMap = new HashMap<>();
            Map<String, Long> customerLastInvoiceTs = new HashMap<>();

            for (com.example.tlotlotau.Documents.Invoice.Invoice inv : invoices) {
                // invoice total
                double invTotal = 0.0;
                try { invTotal = inv.getTotalAmount(); } catch (Exception ignored) {}
                totalInvoiceValue += invTotal;
                invoiceCount++;

                String cname = inv.getCustomerName() == null ? "" : inv.getCustomerName();
                // accumulate per-customer amount
                double existing = customerDueMap.containsKey(cname) ? customerDueMap.get(cname) : 0.0;
                customerDueMap.put(cname, existing + invTotal);

                // parse invoice timestamp (stored as millis string in DB schema)
                long ts = 0L;
                try { ts = Long.parseLong(inv.getTimestamp()); } catch (Exception ignored) {}
                Long prev = customerLastInvoiceTs.get(cname);
                if (prev == null || ts > prev) customerLastInvoiceTs.put(cname, ts);

                // aging bucket for the invoice (we treat invoice age by days)
                long ageDays = (ts > 0) ? Math.max(0L, (now - ts) / DAY_MS) : 0L;
                if (ageDays <= 30) aging.put("0-30", aging.get("0-30") + 1);
                else if (ageDays <= 60) aging.put("31-60", aging.get("31-60") + 1);
                else if (ageDays <= 90) aging.put("61-90", aging.get("61-90") + 1);
                else aging.put("90+", aging.get("90+") + 1);
            }

            // compute totals from per-customer map
            for (Customer c : customers) {
                String cname = c.getName() == null ? "" : c.getName();
                double due = customerDueMap.containsKey(cname) ? customerDueMap.get(cname) : 0.0;
                totalDue += due;
                if (due > 0) withDueCount++; else noDueCount++;
            }

            final double finalTotalDue = totalDue;
            final int finalWithDue = withDueCount;
            final int finalNoDue = noDueCount;
            final int finalTotalCustomers = totalCustomers;
            final double avgInvoice = invoiceCount == 0 ? 0.0 : (totalInvoiceValue / invoiceCount);

            // prepare top debtors list (sorted)
            List<Map.Entry<String, Double>> debtList = new ArrayList<>(customerDueMap.entrySet());
            debtList.removeIf(e -> e.getValue() <= 0.0);
            Collections.sort(debtList, (a, b) -> Double.compare(b.getValue(), a.getValue()));

            // Build display list for recycler: we'll attach amountDue + last invoice ts into Customer objects' fields
            List<Customer> displayList = new ArrayList<>();
            for (Customer c : customers) {
                String cname = c.getName() == null ? "" : c.getName();
                double due = customerDueMap.containsKey(cname) ? customerDueMap.get(cname) : 0.0;
                c.setAmountDue(String.format(Locale.getDefault(), "%.2f", due));
                Long lastTs = customerLastInvoiceTs.get(cname);
                if (lastTs != null) c.setLastInvoiceTs(String.valueOf(lastTs));
                displayList.add(c);
            }

            // pass to UI thread
            requireActivity().runOnUiThread(() -> {
                // KPIs
                tvCount.setText(String.format(Locale.getDefault(), "%d customers", finalTotalCustomers));
                tvTotalDue.setText(String.format(Locale.getDefault(), "Total due: %s", money.format(finalTotalDue)));
                tvOverdueCount.setText(String.format(Locale.getDefault(), "Overdue customers: %d", countCustomersOverdue(displayList)));
                tvAvgInvoice.setText(String.format(Locale.getDefault(), "Avg invoice: %s", money.format(avgInvoice)));
                tvTopDebtor.setText(debtList.isEmpty() ? "Top debtor: —" :
                        String.format(Locale.getDefault(), "Top debtor: %s (%s)", debtList.get(0).getKey(), money.format(debtList.get(0).getValue())));

                // recycler
                CustomersAdapter adapter = new CustomersAdapter(displayList);
                recycler.setAdapter(adapter);

                // pie chart: With Due vs No Due
                ArrayList<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(finalWithDue, "With Due"));
                entries.add(new PieEntry(finalNoDue, "No Due"));
                PieDataSet ds = new PieDataSet(entries, "Customer status");
                // use material + pastel for clarity
                int[] colors = new int[] { ColorTemplate.MATERIAL_COLORS[0], ColorTemplate.PASTEL_COLORS[1] };
                ds.setColors(colors);
                PieData pd = new PieData(ds);
                pd.setValueTextSize(12f);
                pieChart.setData(pd);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText(String.format(Locale.getDefault(), "%d customers", finalTotalCustomers));
                pieChart.invalidate();
            });

        } catch (Exception e) {
            e.printStackTrace();
            // optionally show an error on UI
            requireActivity().runOnUiThread(() -> {
                tvCount.setText("Error loading data");
            });
        }
    }


    private int countCustomersOverdue(List<Customer> displayList) {
        int overdue = 0;
        final long now = System.currentTimeMillis();
        final long DAY_MS = 24L * 60L * 60L * 1000L;
        for (Customer c : displayList) {
            try {
                String tsStr = c .getLastInvoiceTs();
                if (tsStr == null) continue;
                long ts = Long.parseLong(tsStr);
                long ageDays = (now - ts) / DAY_MS;
                double due = 0.0;
                try { due = Double.parseDouble(c.getAmountDue()); } catch (Exception ignored) {}
                if (due > 0 && ageDays > 30) overdue++;
            } catch (Exception ignored) {}
        }
        return overdue;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // shut down executor to avoid leaks
        executor.shutdownNow();
        if (db != null) db.close();
    }
}
