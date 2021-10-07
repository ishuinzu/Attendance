package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.databinding.ActivityAddStudentBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Student;

public class AddStudentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddStudentActivity";
    private ActivityAddStudentBinding binding;
    private String department, section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
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
        binding.cardAddStudent.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.cardAddStudent:
                // Add Student
                addStudent();
                break;
        }
    }

    private void addStudent() {
        // Get Details
        String name = binding.edtStudentName.getText().toString();
        String roll_number = binding.edtRollNumber.getText().toString();
        String father_name = binding.edtFatherName.getText().toString();
        String father_phone_number = binding.edtFatherPhoneNumber.getText().toString();
        String address = binding.edtAddress.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(AddStudentActivity.this, "Name Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (roll_number.isEmpty()) {
            Toast.makeText(AddStudentActivity.this, "Roll Number Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (father_name.isEmpty()) {
            Toast.makeText(AddStudentActivity.this, "Father Name Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (father_phone_number.isEmpty()) {
            Toast.makeText(AddStudentActivity.this, "Father Phone Number Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (address.isEmpty()) {
            Toast.makeText(AddStudentActivity.this, "Address Required", Toast.LENGTH_SHORT).show();
            return;
        } else if (father_phone_number.charAt(0) == '0') {
            Toast.makeText(AddStudentActivity.this, "Remove First Digit (0) From Father's Phone Number", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Add Student
            Student student = new Student();
            student.setAddress(address);
            student.setCreation(System.currentTimeMillis());
            student.setDepartment(department);
            student.setFather_name(father_name);
            student.setFather_phone_number("+92" + father_phone_number.replaceAll("\\s+", ""));
            student.setName(name);
            student.setRoll_number(roll_number);
            student.setSection(section);

            // Show Loading
            LoadingDialog.showLoadingDialog(AddStudentActivity.this);

            FirebaseDatabase.getInstance().getReference().child("student")
                    .child(department)
                    .child(section.replaceAll("\\s+", ""))
                    .child("" + student.getCreation())
                    .setValue(student)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Close Dialog
                                LoadingDialog.closeDialog();

                                Toast.makeText(AddStudentActivity.this, "Student Added", Toast.LENGTH_SHORT).show();
                                // Clear Data
                                binding.edtAddress.setText("");
                                binding.edtFatherName.setText("");
                                binding.edtFatherPhoneNumber.setText("");
                                binding.edtStudentName.setText("");
                                binding.edtRollNumber.setText("");
                            }
                        }
                    });
        }
    }
}