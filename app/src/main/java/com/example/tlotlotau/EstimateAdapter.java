package com.example.tlotlotau;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EstimateAdapter extends ArrayAdapter<Estimate> {

    private final LayoutInflater inflater;
    private final List<Estimate> estimates;
    private final Context context;

    public EstimateAdapter(Context context, List<Estimate> estimates) {
        super(context, 0, estimates);
        this.inflater = LayoutInflater.from(context);
        this.estimates = estimates;
        this.context = context;
    }

    private static class ViewHolder {
        TextView customerNameTextView;
        TextView totalAmountTextView;
        TextView timestampTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // Inflate the custom layout for each list item
            convertView = inflater.inflate(R.layout.estimate_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.customerNameTextView = convertView.findViewById(R.id.customerName);
            viewHolder.totalAmountTextView = convertView.findViewById(R.id.totalAmount);
            viewHolder.timestampTextView = convertView.findViewById(R.id.timestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Estimate estimate = estimates.get(position);
        viewHolder.customerNameTextView.setText(estimate.getCustomerName());
        viewHolder.totalAmountTextView.setText(String.format(Locale.getDefault(), "R%.2f", estimate.getTotalAmount()));
        viewHolder.timestampTextView.setText(formatTimestamp(estimate.getTimestamp()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(context, DocumentDetailActivity.class);
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_DOCUMENT_TYPE, "invoice");
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_FILE_PATH, estimate.getFilePath());
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_CUSTOMER_NAME, estimate.getCustomerName());
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_TOTAL_AMOUNT, estimate.getTotalAmount());
                context.startActivity(detailIntent);
            }
        });

        return convertView;

    }

    private String formatTimestamp(String timestamp) {
        if (timestamp == null) {
            Log.e("EstimateAdapter", "Timestamp is null");
            return "N/A";
        }
        try {
            long timestampLong = Long.parseLong(timestamp);
            Date date = new Date(timestampLong);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            Log.e("EstimateAdapter", "Error parsing timestamp: " + timestamp, e);
            return "Invalid timestamp";
        }
    }
}
