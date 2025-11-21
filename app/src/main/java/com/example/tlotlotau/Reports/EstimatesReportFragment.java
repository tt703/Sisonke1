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
import com.example.tlotlotau.Documents.Estimate.Estimate;
import com.example.tlotlotau.R;
import com.example.tlotlotau.Reports.EstimateAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EstimatesReportFragment extends Fragment {
    private DatabaseHelper db;
    private BarChart barChart;
    private RecyclerView recycler;
    private TextView tvCount, tvTotal;

    public EstimatesReportFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estimates_report, container, false);

        db = new DatabaseHelper(requireContext());

        barChart = v.findViewById(R.id.barChartEstimates);
        recycler = v.findViewById(R.id.recyclerEstimates);
        tvCount = v.findViewById(R.id.tvEstimatesCount);
        tvTotal = v.findViewById(R.id.tvEstimatesTotal);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadEstimates();

        return v;
    }

    private void loadEstimates() {
        List<Estimate> items = db.getAllEstimates();
        if (items == null) items = new ArrayList<>();

        // compute totals safely
        double total = 0.0;
        for (Estimate e : items) {
            if (e == null) continue;
            try {
                total += e.getTotalAmount();
            } catch (Exception ignored) { }
        }

        tvCount.setText(String.format(Locale.getDefault(), "%d estimates", items.size()));
        tvTotal.setText(String.format(Locale.getDefault(), "Total: R %.2f", total));

        // Recycler adapter (defensive)
        EstimateAdapter adapter = new EstimateAdapter(items == null ? new ArrayList<>() : items);
        recycler.setAdapter(adapter);

        // Prepare chart entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        int idx = 0;
        for (Estimate e : items) {
            if (e == null) continue;
            float val = 0f;
            try { val = (float) e.getTotalAmount(); } catch (Exception ignored) {}
            entries.add(new BarEntry(idx++, val));
        }

        if (entries.isEmpty()) {
            barChart.clear();
            barChart.setNoDataText("No estimates to display");
        } else {
            BarDataSet ds = new BarDataSet(entries, "Estimate amounts");
            ds.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData data = new BarData(ds);
            data.setBarWidth(0.9f);

            barChart.setData(data);
            if (barChart.getDescription() != null) barChart.getDescription().setEnabled(false);
            barChart.setFitBars(true);
            barChart.invalidate();
        }
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
