package com.example.tlotlotau.Documents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import com.example.tlotlotau.DatabaseHelper;
import com.example.tlotlotau.R;

import java.util.List;
public class InvoicesFragment extends Fragment {
    private ListView invoicesListView;
    private InvoiceAdapter invoiceAdapter;
    private List<Invoice> invoices;
    private DatabaseHelper dbHelper;

    public InvoicesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_invoices, container, false);
        invoicesListView = view.findViewById(R.id.invoicesListView);
        dbHelper = new DatabaseHelper(getContext());
        loadInvoices();
        return view;
    }
    private void loadInvoices(){
        invoices = dbHelper.getAllInvoices();
        invoiceAdapter = new InvoiceAdapter(getContext(), invoices);
        invoicesListView.setAdapter(invoiceAdapter);
        Log.d("InvoicesFragment", "Loaded " + invoices.size() + " invoices");
    }
}
