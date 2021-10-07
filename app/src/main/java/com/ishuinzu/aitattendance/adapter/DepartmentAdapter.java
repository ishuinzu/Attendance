package com.ishuinzu.aitattendance.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.object.Department;

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {
    private Context context;
    private List<Department> departments;
    private LayoutInflater layoutInflater;

    public DepartmentAdapter(Context context, List<Department> departments) {
        this.context = context;
        this.departments = departments;
        this.layoutInflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_department, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtDepartmentName.setText(departments.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardDepartment;
        private final TextView txtDepartmentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDepartment = itemView.findViewById(R.id.cardDepartment);
            txtDepartmentName = itemView.findViewById(R.id.txtDepartmentName);
        }
    }
}