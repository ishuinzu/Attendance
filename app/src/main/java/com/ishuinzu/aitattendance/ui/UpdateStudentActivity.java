package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.databinding.ActivityUpdateStudentBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Student;

public class UpdateStudentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdateStudentActivity";
    private ActivityUpdateStudentBinding binding;
    private String department;
    private String section;
    private Long creation;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        // Get Data
        if (getIntent().getExtras() != null) {
            department = getIntent().getExtras().getString("DEPARTMENT");
            section = getIntent().getExtras().getString("SECTION");
            creation = getIntent().getExtras().getLong("CREATION");
        }

        // Set Sub Title
        binding.txtSubTitle.setText("Department : " + department + " & Section : " + section);

        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);
        binding.cardUpdateStudent.setOnClickListener(this);

        getStudent();
    }

    private void getStudent() {
        // Show Loading
        LoadingDialog.showLoadingDialog(UpdateStudentActivity.this);

        FirebaseDatabase.getInstance().getReference().child("student")
                .child(department)
                .child(section.replaceAll("\\s+", ""))
                .child("" + creation)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getChildrenCount() != 0) {
                                student = snapshot.getValue(Student.class);

                                if (student != null) {
                                    binding.edtStudentName.setText(student.getName());
                                    binding.edtRollNumber.setText(student.getRoll_number());
                                    binding.edtFatherName.setText(student.getFather_name());
                                    binding.edtPhoneNumber01.setText(student.getPhone_number_01().substring(3));
                                    binding.edtPhoneNumber02.setText(student.getPhone_number_02().substring(3));
                                    binding.edtAddress.setText(student.getAddress());
                                }
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.cardUpdateStudent:
                // Update Student
                updateStudent();
                break;
        }
    }

    private void updateStudent() {
        String name = binding.edtStudentName.getText().toString();
        String roll_number = binding.edtRollNumber.getText().toString();
        String father_name = binding.edtFatherName.getText().toString();
        String phone_number_01 = binding.edtPhoneNumber01.getText().toString();
        String phone_number_02 = binding.edtPhoneNumber02.getText().toString();
        String address = binding.edtAddress.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(UpdateStudentActivity.this, "Student Name Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (roll_number.isEmpty()) {
            Toast.makeText(UpdateStudentActivity.this, "Roll Number Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (father_name.isEmpty()) {
            Toast.makeText(UpdateStudentActivity.this, "Father Name Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone_number_01.isEmpty()) {
            Toast.makeText(UpdateStudentActivity.this, "Phone Number 01 Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone_number_02.isEmpty()) {
            Toast.makeText(UpdateStudentActivity.this, "Phone Number 01 Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (address.isEmpty()) {
            Toast.makeText(UpdateStudentActivity.this, "Address Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone_number_01.charAt(0) == '0') {
            Toast.makeText(UpdateStudentActivity.this, "Remove First Digit (0) From Phone Number 01", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone_number_02.charAt(0) == '0') {
            Toast.makeText(UpdateStudentActivity.this, "Remove First Digit (0) From Phone Number 02", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.equals(student.getName()) && roll_number.equals(student.getRoll_number()) && father_name.equals(student.getFather_name()) && phone_number_01.equals(student.getPhone_number_01().substring(3)) && phone_number_02.equals(student.getPhone_number_02().substring(3)) && address.equals(student.getAddress())) {
            Toast.makeText(UpdateStudentActivity.this, "No Changes", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Show Loading
            LoadingDialog.showLoadingDialog(UpdateStudentActivity.this);

            Student newStudent = new Student();
            newStudent.setName(name);
            newStudent.setSection(section);
            newStudent.setDepartment(department);
            newStudent.setRoll_number(roll_number);
            newStudent.setCreation(creation);
            newStudent.setFather_name(father_name);
            newStudent.setAddress(address);
            newStudent.setPhone_number_01("+92" + phone_number_01);
            newStudent.setPhone_number_02("+92" + phone_number_02);

            FirebaseDatabase.getInstance().getReference().child("student")
                    .child(department)
                    .child(section.replaceAll("\\s+", ""))
                    .child("" + creation)
                    .setValue(newStudent)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Close Loading
                                LoadingDialog.closeDialog();

                                Toast.makeText(UpdateStudentActivity.this, "Student Updated", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }
    }
}