package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Locale;

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
        binding.cardAddDepartment.setOnClickListener(this);

        getDepartments();
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCloseScreen) {
            // Close Screen
            finish();
        } else if (view.getId() == R.id.cardAddDepartment) {
            // Add Department Dialog
            Dialog dialog = new Dialog(ManageDepartmentsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_add_department);
            dialog.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextInputEditText edtDepartmentName = dialog.findViewById(R.id.edtDepartmentName);
            (dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            (dialog.findViewById(R.id.btnAdd)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String departmentName = edtDepartmentName.getText().toString();

                    if (!departmentName.isEmpty()) {
                        // Check For Department Name
                        FirebaseDatabase.getInstance().getReference().child("department")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult() != null) {
                                                String lastDepartmentID = "0";
                                                Boolean shouldAdd = true;

                                                for (DataSnapshot departmentData : task.getResult().getChildren()) {
                                                    Department department = departmentData.getValue(Department.class);

                                                    if (department != null) {
                                                        lastDepartmentID = departmentData.getKey();
                                                        if (department.getName().toLowerCase(Locale.getDefault()).equals(departmentName.toLowerCase(Locale.getDefault()))) {
                                                            shouldAdd = false;
                                                        }
                                                    }
                                                }

                                                if (shouldAdd) {
                                                    if (lastDepartmentID != null) {
                                                        int lastID = Integer.parseInt(lastDepartmentID);
                                                        Department newDepartment = new Department();
                                                        newDepartment.setName(departmentName);
                                                        newDepartment.setId(lastID + 1);
                                                        FirebaseDatabase.getInstance().getReference().child("department")
                                                                .child("" + newDepartment.getId())
                                                                .setValue(newDepartment)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(ManageDepartmentsActivity.this, "Department Added", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                } else {
                                                    Toast.makeText(ManageDepartmentsActivity.this, "Department Name Already Exist", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(ManageDepartmentsActivity.this, "Department Name Required", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
            dialog.show();
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