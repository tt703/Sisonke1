package com.example.tlotlotau.Documents.Invoice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tlotlotau.Documents.DocumentDetailActivity;
import com.example.tlotlotau.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceAdapter extends ArrayAdapter<Invoice> {

    private final LayoutInflater inflater;

    private final List<Invoice> invoices;
    private final Context context;

    public InvoiceAdapter(Context context, List<Invoice> invoices) {
        super(context, 0, invoices);
        this.inflater = LayoutInflater.from(context);
        this.invoices = invoices;
        this.context = context;
    }

    private static class ViewHolder {
        TextView customerNameTextView;
        TextView totalAmountTextView;
        TextView timestampTextView;
        //TextView status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.invoice_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.customerNameTextView = convertView.findViewById(R.id.customerName);
            viewHolder.totalAmountTextView = convertView.findViewById(R.id.totalAmount);
            viewHolder.timestampTextView = convertView.findViewById(R.id.timestamp);
            //viewHolder.status=convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Invoice invoice = invoices.get(position);
        viewHolder.customerNameTextView.setText(invoice.getCustomerName());
        viewHolder.totalAmountTextView.setText(String.format(Locale.getDefault(), "R%.2f", invoice.getTotalAmount()));
        viewHolder.timestampTextView.setText(formatTimestamp(invoice.getTimestamp()));
        //viewHolder.status.setText(invoice.getStatus());

        // Set click listener to open DocumentDetailActivity
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(context, DocumentDetailActivity.class);
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_DOCUMENT_TYPE, "invoice");
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_FILE_PATH, invoice.getFilePath());
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_CUSTOMER_NAME, invoice.getCustomerName());
                detailIntent.putExtra(DocumentDetailActivity.EXTRA_TOTAL_AMOUNT, invoice.getTotalAmount());
                context.startActivity(detailIntent);
            }
        });

        return convertView;
    }

    private String formatTimestamp(String timestamp) {
        if (timestamp == null) {
            Log.e("InvoiceAdapter", "Timestamp is null");
            return "N/A";
        }
        try {
            long timestampLong = Long.parseLong(timestamp);
            Date date = new Date(timestampLong);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            Log.e("InvoiceAdapter", "Error parsing timestamp: " + timestamp, e);
            return "Invalid timestamp";
        }
    }
}
