package com.ishuinzu.aitattendance.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.adapter.StudentAdapter;
import com.ishuinzu.aitattendance.databinding.ActivitySendSmsBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Student;

import java.util.ArrayList;
import java.util.List;

public class SendSMSActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SendSMSActivity";
    private static final int REQUEST_CODE = 100;
    private ActivitySendSmsBinding binding;
    private String department, section;
    private List<Boolean> selections;
    private List<Student> students;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendSmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        // Get Intent Data
        if (getIntent().getExtras() != null) {
            department = getIntent().getExtras().getString("DEPARTMENT");
            section = getIntent().getExtras().getString("SECTION");
        }

        // Set Sub Title
        binding.txtSubTitle.setText("Department : " + department + " & Section : " + section);

        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);

        // Sent Message Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Ask Permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE);
            }
        }

        getStudents();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getStudents() {
        // Show Loading
        LoadingDialog.showLoadingDialog(SendSMSActivity.this);
        selections = new ArrayList<>();
        students = new ArrayList<>();
        studentAdapter = new StudentAdapter(SendSMSActivity.this, selections, students);
        binding.recyclerStudents.setAdapter(studentAdapter);

        // Get Students
        FirebaseDatabase.getInstance().getReference().child("student")
                .child(department)
                .child(section.replaceAll("\\s+", ""))
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            selections.clear();
                            students.clear();

                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Student student = dataSnapshot.getValue(Student.class);

                                    if (student != null) {
                                        selections.add(false);
                                        students.add(student);
                                    }
                                }
                                studentAdapter.notifyDataSetChanged();
                                // Close Loading
                                LoadingDialog.closeDialog();
                            } else {
                                // Close Loading
                                LoadingDialog.closeDialog();

                                Toast.makeText(SendSMSActivity.this, "No Result Found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Close Loading
                            LoadingDialog.closeDialog();

                            Toast.makeText(SendSMSActivity.this, "No Result Found", Toast.LENGTH_SHORT).show();
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