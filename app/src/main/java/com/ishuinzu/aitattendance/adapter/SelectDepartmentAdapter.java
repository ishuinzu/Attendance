package com.ishuinzu.aitattendance.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.object.Department;
import com.ishuinzu.aitattendance.ui.HODDetailsActivity;
import com.ishuinzu.aitattendance.ui.TeacherDetailsActivity;

import java.util.List;

public class SelectDepartmentAdapter extends RecyclerView.Adapter<SelectDepartmentAdapter.ViewHolder> {
    private Context context;
    private List<Department> departments;
    private LayoutInflater layoutInflater;
    private String userType;

    public SelectDepartmentAdapter(Context context, List<Department> departments, String userType) {
        this.context = context;
        this.departments = departments;
        this.userType = userType;
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
        return new ViewHolder(layoutInflater.inflate(R.layout.item_select_department, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.cardSelectDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("Teacher")) {
                    context.startActivity(new Intent(context, TeacherDetailsActivity.class).putExtra("DEPARTMENT", departments.get(position).getName()));
                } else if (userType.equals("HOD")) {
                    context.startActivity(new Intent(context, HODDetailsActivity.class).putExtra("DEPARTMENT", departments.get(position).getName()));
                }
            }
        });
        holder.txtDepartmentName.setText(departments.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardSelectDepartment;
        private final TextView txtDepartmentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSelectDepartment = itemView.findViewById(R.id.cardSelectDepartment);
            txtDepartmentName = itemView.findViewById(R.id.txtDepartmentName);
        }
    }
}