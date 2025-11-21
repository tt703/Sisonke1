package com.example.tlotlotau.Customer;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tlotlotau.Database.DatabaseHelper;
import com.example.tlotlotau.R;

public class CreateCustomerFragment extends Fragment {
    public interface OnCustomerCreatedListener {
        void onCustomerCreated(@NonNull Customer customer);
    }
    private OnCustomerCreatedListener listener;

    private EditText etName, etAddress, etPhone, etEmail;
    private Button btnSave;

    public static CreateCustomerFragment newInstance() {
        return new CreateCustomerFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCustomerCreatedListener) {
            listener = (OnCustomerCreatedListener) context;
        } else {
            throw new RuntimeException("Activity must implement OnCustomerCreatedListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_customer, container, false);
        etName = view.findViewById(R.id.etCustomerName);
        etEmail = view.findViewById(R.id.etCustomerEmail);
        etPhone = view.findViewById(R.id.etCustomerPhone);
        etAddress = view.findViewById(R.id.etCustomerAddress);
        btnSave = view.findViewById(R.id.btnSaveCustomer);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();// 1. Check Customer Name

            if (TextUtils.isEmpty(name)) {
                etName.setError("Customer name is required");
                etName.requestFocus(); // Move cursor to the problematic field
                return;
            }



            // 5. Optional but recommended: Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Please enter a valid email address");
                etEmail.requestFocus();
                return;
            }
            // 2. Check Customer Phone
            if (TextUtils.isEmpty(phone)) {
                etPhone.setError("Phone number is required");
                etPhone.requestFocus();
                return;
            }
            String saPhonePattern = "^(0\\d{9})$";
            if (!phone.matches(saPhonePattern)) {
                etPhone.setError("Please enter a valid 10-digit South African phone number (e.g., 0821234567)");
                etPhone.requestFocus();
                return;
            }

            // 6. Check Customer Address
            if (TextUtils.isEmpty(address)) {
                etAddress.setError("Address is required");
                etAddress.requestFocus();
                return;
            }


            Context context = getContext();
            if (context == null) {
                Toast.makeText(getActivity(), "Context not available", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = new DatabaseHelper(context);
            long newId = -1;
            try {
                String now = String.valueOf(System.currentTimeMillis());
                newId = db.insertCustomer(name, phone, email, address, now);
            } finally {
                db.close();
            }

            if (newId != -1) {
                Customer newCustomer = new Customer(newId, name, address, phone, email);
                newCustomer.setAmountDue("0.0");
                newCustomer.setNumEstimateSent("0");

                Toast.makeText(getContext(), "Customer created", Toast.LENGTH_SHORT).show();

                if (listener != null) listener.onCustomerCreated(newCustomer);

                etName.setText("");
                etEmail.setText("");
                etPhone.setText("");
                etAddress.setText("");
            } else {
                Toast.makeText(getContext(), "Error creating customer", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
