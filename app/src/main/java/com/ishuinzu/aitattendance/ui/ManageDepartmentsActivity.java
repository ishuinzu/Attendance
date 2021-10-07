package com.ishuinzu.aitattendance.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.adapter.DepartmentAdapter;
import com.ishuinzu.aitattendance.databinding.ActivityManageDepartmentsBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Department;

import java.util.ArrayList;
import java.util.List;

public class ManageDepartmentsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ManageDepartmentsActivity";
    private ActivityManageDepartmentsBinding binding;
    private List<Department> departments;
    private DepartmentAdapter departmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageDepartmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);

        getDepartments();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCloseScreen) {
            // Close Screen
            finish();
        }
    }

    private void getDepartments() {
        departments = new ArrayList<>();
        departmentAdapter = new DepartmentAdapter(ManageDepartmentsActivity.this, departments);
        binding.recyclerDepartments.setAdapter(departmentAdapter);

        loadDepartments();
    }

    private void loadDepartments() {
        // Show Loading
        LoadingDialog.showLoadingDialog(ManageDepartmentsActivity.this);

        FirebaseDatabase.getInstance().getReference().child("department")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        departments.clear();

                        if (snapshot.exists()) {
                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Department department = dataSnapshot.getValue(Department.class);

                                    if (department != null) {
                                        departments.add(department);
                                    }
                                }
                                departmentAdapter.notifyDataSetChanged();

                                // Close Loading
                                LoadingDialog.closeDialog();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Close Loading
                        LoadingDialog.closeDialog();
                    }
                });
    }
}