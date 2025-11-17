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
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;

public class EstimatesReportFragment extends Fragment {
    private DatabaseHelper db;
    private BarChart barChart;
    private RecyclerView recycler;
    private TextView tvCount, tvTotal;

    public EstimatesReportFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
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
        double total = 0;
        for (Estimate e : items) total += e.getTotalAmount();
        tvCount.setText(String.format("%d estimates", items.size()));
        tvTotal.setText(String.format("Total: R %.2f", total));

        EstimateAdapter adapter = new EstimateAdapter(items);
        recycler.setAdapter(adapter);

        ArrayList<BarEntry> entries = new ArrayList<>();
        int idx = 0;
        for (Estimate e : items) entries.add(new BarEntry(idx++, (float) e.getTotalAmount()));
        BarDataSet ds = new BarDataSet(entries, "Estimate amounts");
        ds.setColors(ColorTemplate.MATERIAL_COLORS);
        barChart.setData(new BarData(ds));
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }
}
