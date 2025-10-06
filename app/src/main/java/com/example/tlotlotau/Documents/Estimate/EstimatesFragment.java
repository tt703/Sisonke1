package com.example.tlotlotau.Documents.Estimate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.widget.ListView;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;

import java.util.List;
public class EstimatesFragment extends Fragment{

    private ListView estimatesListView;
    private EstimateAdapter estimateAdapter;
    private List<CreateEstimateActivity.Estimate> estimates;
    private DatabaseHelper dbHelper;

    public EstimatesFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_estimates, container, false);
        estimatesListView = view.findViewById(R.id.estimatesListView);
        dbHelper = new DatabaseHelper(getContext());
        loadEstimates();
        return view;
    }
    private void loadEstimates(){
        estimates = dbHelper.getAllEstimates();
        estimateAdapter = new EstimateAdapter(getContext(), estimates);
        estimatesListView.setAdapter(estimateAdapter);
        Log.d("EstimatesFragment", "Loaded " + estimates.size() + " estimates");
    }

}
