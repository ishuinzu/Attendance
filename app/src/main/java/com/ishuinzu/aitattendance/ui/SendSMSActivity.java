package com.ishuinzu.aitattendance.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.adapter.StudentAdapter;
import com.ishuinzu.aitattendance.databinding.ActivitySendSmsBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.MessageType;
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
    private List<String> messageTypeNames;
    private ArrayAdapter<String> messageTypesArrayAdapter;
    private String messageType;

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
        binding.cardSendAll.setOnClickListener(this);

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

            case R.id.cardSendAll:
                // Send All
                sendAll();
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void sendAll() {
        if (students.size() != 0) {
            // Message Dialog
            Dialog dialog = new Dialog(SendSMSActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_message_type);
            dialog.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            AutoCompleteTextView selectMessageType = dialog.findViewById(R.id.selectMessageType);
            TextInputEditText edtMessage = dialog.findViewById(R.id.edtMessage);
            selectMessageType.setOnItemClickListener((adapterView, view12, i, l) -> {
                messageType = messageTypeNames.get(i);
                dialog.findViewById(R.id.edtMessageLayout).setEnabled(!messageType.equals("Select Message Type"));
            });
            (dialog.findViewById(R.id.btnCancel)).setOnClickListener(view1 -> dialog.dismiss());
            (dialog.findViewById(R.id.btnSend)).setOnClickListener(view13 -> {
                if (!messageType.equals("Select Message Type")) {
                    String message = edtMessage.getText().toString();
                    if (!message.isEmpty()) {
                        // Send SMS
                        for (Student student : students) {
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(student.getFather_phone_number(), null, messageType + "\n\n" + message, null, null);
                                Toast.makeText(SendSMSActivity.this, "Done", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(SendSMSActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    } else {
                        Toast.makeText(SendSMSActivity.this, "Message Required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SendSMSActivity.this, "Message Type Required", Toast.LENGTH_SHORT).show();
                }
            });
            messageTypeNames = new ArrayList<>();
            messageTypesArrayAdapter = new ArrayAdapter<>(SendSMSActivity.this, R.layout.item_drop_down, R.id.txtItem, messageTypeNames);
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
        } else {
            Toast.makeText(SendSMSActivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
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