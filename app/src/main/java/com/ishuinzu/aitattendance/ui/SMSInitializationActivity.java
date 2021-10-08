package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.app.Utils;
import com.ishuinzu.aitattendance.databinding.ActivitySmsInitializationBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Department;
import com.ishuinzu.aitattendance.object.MessageType;
import com.ishuinzu.aitattendance.object.SMS;
import com.ishuinzu.aitattendance.object.Section;
import com.ishuinzu.aitattendance.object.Student;

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
    private List<String> messageTypeNames;
    private ArrayAdapter<String> messageTypesArrayAdapter;
    private String messageType;

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
            } else if (section.equals("All Sections")) {
                // Show Dialog
                Dialog dialog = new Dialog(SMSInitializationActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_message_type);
                dialog.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                AutoCompleteTextView selectMessageType = dialog.findViewById(R.id.selectMessageType);
                TextInputEditText edtMessage = dialog.findViewById(R.id.edtMessage);
                selectMessageType.setOnItemClickListener((adapterView1, view12, i1, l1) -> {
                    messageType = messageTypeNames.get(i1);
                    dialog.findViewById(R.id.edtMessageLayout).setEnabled(!messageType.equals("Select Message Type"));
                });
                (dialog.findViewById(R.id.btnCancel)).setOnClickListener(view1 -> dialog.dismiss());
                (dialog.findViewById(R.id.btnSend)).setOnClickListener(view13 -> {
                    if (!messageType.equals("Select Message Type")) {
                        String message = edtMessage.getText().toString();
                        if (!message.isEmpty()) {
                            // Show Loading
                            LoadingDialog.showLoadingDialog(SMSInitializationActivity.this);

                            // Send Message To Each Student
                            FirebaseDatabase.getInstance().getReference().child("student")
                                    .child(department)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            if (task.getResult() != null) {
                                                if (task.getResult().getChildrenCount() != 0) {
                                                    for (DataSnapshot section : task.getResult().getChildren()) {
                                                        if (section.getChildrenCount() != 0) {
                                                            for (DataSnapshot studentData : section.getChildren()) {
                                                                Student student = studentData.getValue(Student.class);

                                                                if (student != null) {
                                                                    try {
                                                                        SmsManager smsManager = SmsManager.getDefault();
                                                                        smsManager.sendTextMessage(student.getFather_phone_number(), null, messageType + "\n\n" + message, null, null);

                                                                        // Save Message To Database
                                                                        SMS sms = new SMS();
                                                                        sms.setAddress(student.getAddress());
                                                                        sms.setBy_name(Preferences.getInstance(SMSInitializationActivity.this).getName());
                                                                        sms.setBy_type(Preferences.getInstance(SMSInitializationActivity.this).getType());
                                                                        sms.setDepartment(student.getDepartment());
                                                                        sms.setCreation(System.currentTimeMillis());
                                                                        sms.setName(student.getName());
                                                                        sms.setFather_name(student.getFather_name());
                                                                        sms.setMessage_text(message);
                                                                        sms.setSection(student.getSection());
                                                                        sms.setRoll_number(student.getRoll_number());
                                                                        sms.setFather_phone_number(student.getFather_phone_number());
                                                                        sms.setMessage_type(messageType);

                                                                        FirebaseDatabase.getInstance().getReference().child("sms")
                                                                                .child(Utils.getDateID())
                                                                                .child(student.getDepartment())
                                                                                .child(student.getSection().replaceAll("\\s+", ""))
                                                                                .child("" + sms.getCreation())
                                                                                .setValue(sms)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(SMSInitializationActivity.this, "Done", Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } catch (Exception e) {
                                                                        Toast.makeText(SMSInitializationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    // Close Loading
                                                    LoadingDialog.closeDialog();

                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SMSInitializationActivity.this, "Message Required", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SMSInitializationActivity.this, "Message Type Required", Toast.LENGTH_SHORT).show();
                    }
                });
                messageTypeNames = new ArrayList<>();
                messageTypesArrayAdapter = new ArrayAdapter<>(SMSInitializationActivity.this, R.layout.item_drop_down, R.id.txtItem, messageTypeNames);
                selectMessageType.setAdapter(messageTypesArrayAdapter);

                FirebaseDatabase.getInstance().getReference().child("message_type")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    messageTypeNames.clear();
                                    messageTypeNames.add("Select Message Type");

                                    if (snapshot.getChildrenCount() != 0) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            MessageType messageType = dataSnapshot.getValue(MessageType.class);

                                            if (messageType != null) {
                                                messageTypeNames.add(messageType.getName());
                                            }
                                        }
                                        dialog.findViewById(R.id.selectMessageTypeLayout).setEnabled(true);
                                        messageTypesArrayAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                dialog.getWindow().setAttributes(layoutParams);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
                dialog.show();
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