package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.adapter.TeacherAdapter;
import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.databinding.ActivityManageTeachersBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Teacher;

import java.util.ArrayList;
import java.util.List;

public class ManageTeachersActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ManageTeachersActivity";
    private ActivityManageTeachersBinding binding;
    private List<Teacher> teachers;
    private TeacherAdapter teacherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageTeachersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);

        getTeachers();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCloseScreen) {
            // Close Screen
            finish();
        }
    }

    private void getTeachers() {
        teachers = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(ManageTeachersActivity.this, teachers, getLayoutInflater());
        binding.recyclerTeachers.setAdapter(teacherAdapter);

        loadTeachers();
    }

    private void loadTeachers() {
        // Show Loading
        LoadingDialog.showLoadingDialog(ManageTeachersActivity.this);

        FirebaseDatabase.getInstance().getReference().child("teacher")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        teachers.clear();

                        if (snapshot.exists()) {
                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);

                                    if (teacher != null) {
                                        if (Preferences.getInstance(ManageTeachersActivity.this).getType().equals("HOD")) {
                                            if (teacher.getDepartment().equals(Preferences.getInstance(ManageTeachersActivity.this).getHODDepartment())) {
                                                teachers.add(teacher);
                                            }
                                        } else {
                                            teachers.add(teacher);
                                        }
                                    }
                                }
                                teacherAdapter.notifyDataSetChanged();

                                // Close Loading
                                LoadingDialog.closeDialog();
                            }
                        } else {
                            // Close Loading
                            LoadingDialog.closeDialog();

                            Toast.makeText(ManageTeachersActivity.this, "No Result Found", Toast.LENGTH_SHORT).show();
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