package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.databinding.ActivitySmsInitializationBinding;
import com.ishuinzu.aitattendance.object.Department;
import com.ishuinzu.aitattendance.object.Section;

import java.util.ArrayList;
import java.util.List;

public class SMSInitializationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SMSInitializationActivity";
    private ActivitySmsInitializationBinding binding;
    private List<String> departmentNames;
    private ArrayAdapter<String> departmentsArrayAdapter;
    private String department;
    private List<String> sectionNames;
    private ArrayAdapter<String> sectionsArrayAdapter;
    private String section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySmsInitializationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);
        binding.cardSendSMS.setOnClickListener(this);
        binding.cardAddStudent.setOnClickListener(this);

        getDepartments();
        getSections();

        department = "Select Department";
        section = "Select Section";
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.cardSendSMS:
                // Set Initialization
                setInitialization();
                break;

            case R.id.cardAddStudent:
                // Add Student
                addStudent();
                break;
        }
    }

    private void getDepartments() {
        departmentNames = new ArrayList<>();
        departmentsArrayAdapter = new ArrayAdapter<>(SMSInitializationActivity.this, R.layout.item_drop_down, R.id.txtItem, departmentNames);
        binding.selectDepartment.setAdapter(departmentsArrayAdapter);
        binding.selectDepartment.setOnItemClickListener((adapterView, view, i, l) -> {
            department = departmentNames.get(i);
            if (department.equals("Select Department")) {
                Toast.makeText(SMSInitializationActivity.this, "Please Select Other Department", Toast.LENGTH_SHORT).show();
                binding.selectDepartmentLayout.setEnabled(true);
                binding.selectSectionLayout.setEnabled(false);
            } else {
                binding.selectSectionLayout.setEnabled(true);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("department")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            departmentNames.clear();
                            departmentNames.add("Select Department");

                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Department department = dataSnapshot.getValue(Department.class);

                                    if (department != null) {
                                        departmentNames.add(department.getName());
                                    }
                                }
                                binding.selectDepartmentLayout.setEnabled(true);
                                departmentsArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getSections() {
        sectionNames = new ArrayList<>();
        sectionsArrayAdapter = new ArrayAdapter<>(SMSInitializationActivity.this, R.layout.item_drop_down, R.id.txtItem, sectionNames);
        binding.selectSection.setAdapter(sectionsArrayAdapter);
        binding.selectSection.setOnItemClickListener((adapterView, view, i, l) -> {
            section = sectionNames.get(i);
            if (section.equals("Select Section")) {
                Toast.makeText(SMSInitializationActivity.this, "Please Select Other Section", Toast.LENGTH_SHORT).show();
                binding.selectSectionLayout.setEnabled(true);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("section")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            sectionNames.clear();
                            sectionNames.add("Select Section");

                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Section section = dataSnapshot.getValue(Section.class);

                                    if (section != null) {
                                        sectionNames.add(section.getName());
                                    }
                                }
                                sectionsArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setInitialization() {
        if (department.isEmpty() || department.equals("Select Department")) {
            Toast.makeText(SMSInitializationActivity.this, "Department Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (section.isEmpty() || section.equals("Select Section")) {
            Toast.makeText(SMSInitializationActivity.this, "Section Required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Start Message Send Activity
            startActivity(new Intent(SMSInitializationActivity.this, SendSMSActivity.class).putExtra("DEPARTMENT", department).putExtra("SECTION", section));
        }
    }

    private void addStudent() {
        if (department.isEmpty() || department.equals("Select Department")) {
            Toast.makeText(SMSInitializationActivity.this, "Department Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (section.isEmpty() || section.equals("Select Section")) {
            Toast.makeText(SMSInitializationActivity.this, "Section Required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Start Add Student Activity
            startActivity(new Intent(SMSInitializationActivity.this, AddStudentActivity.class).putExtra("DEPARTMENT", department).putExtra("SECTION", section));
        }
    }
}