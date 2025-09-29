package com.example.tlotlotau.Employees;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlotlotau.R;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Employee employee);
    }

    private final List<Employee> employees;
    private final OnItemClickListener listener;

    public EmployeeAdapter(List<Employee> employees, OnItemClickListener listener) {
        this.employees = employees;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.bind(employee, listener);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final TextView employeeName;
        private final TextView email;
        private final TextView role;
        private final TextView status;
        private final ImageView moreIcon;

        EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employeeName);
            email = itemView.findViewById(R.id.email);
            role = itemView.findViewById(R.id.role);
            status = itemView.findViewById(R.id.status);
            moreIcon = itemView.findViewById(R.id.moreIcon);
        }

        void bind(Employee employee, OnItemClickListener listener) {
            employeeName.setText(getSafeText(employee.name, employee.email));
            email.setText(getSafeText(employee.email, ""));
            role.setText(getSafeText(employee.role, ""));

            boolean isActive = employee != null && employee.isActive;
            status.setText(isActive ? "Active" : "Disabled");
            int colorRes = isActive ? R.color.green : R.color.red;
            status.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(employee);
            });
        }

        private String getSafeText(String primary, String fallback) {
            return (primary != null && !primary.isEmpty()) ? primary : fallback;
        }
    }
}
