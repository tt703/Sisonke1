package com.example.tlotlotau.Employees;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlotlotau.R;

import java.util.List;
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Employee employee);

    }

    private final List<Employee> items;
    private final OnItemClickListener listener;

    public EmployeeAdapter(List<Employee> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item, parent, false);
        return new VH(v);
    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Employee e = items.get(position);
        holder.employeeName.setText(e.name != null ? e.name : e.email);
        holder.email.setText(e.email != null ? e.email : "");
        holder.role.setText(e.role != null ? e.role : "");
        holder.status.setText(e.isActive ? "Active" : "Disabled");

        holder.itemView.setOnClickListener(v -> {
            if(listener != null) listener.onItemClick(e);
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView employeeName, email, role, status;
        ImageView moreIcon;
        VH(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employeeName);
            email = itemView.findViewById(R.id.email);
            role = itemView.findViewById(R.id.role);
            status = itemView.findViewById(R.id.status);
            moreIcon = itemView.findViewById(R.id.moreIcon);
        }
    }

}
