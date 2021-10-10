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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.adapter.RecyclerViewItemClickListener;
import com.ishuinzu.aitattendance.adapter.StudentAdapter;
import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.app.Utils;
import com.ishuinzu.aitattendance.databinding.ActivitySendSmsBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.MessageType;
import com.ishuinzu.aitattendance.object.SMS;
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
    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendSmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
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
        binding.cardSelectAll.setOnClickListener(this);
        binding.cardMessage.setOnClickListener(this);

        if (Preferences.getInstance(SendSMSActivity.this).getDialogInstructions01()) {
            askPermission();
        } else {
            // Show Instruction 01 Dialog
            Dialog dialog = new Dialog(SendSMSActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_instructions_01);
            dialog.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            (dialog.findViewById(R.id.btnDoNotShowAgain)).setOnClickListener(view -> {
                Preferences.getInstance(SendSMSActivity.this).setDialogInstructionsO1(true);
                dialog.dismiss();
            });
            (dialog.findViewById(R.id.btnIKnow)).setOnClickListener(view1 -> {
                askPermission();
                dialog.dismiss();
            });

            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
            dialog.show();
        }

        selected = 0;
        binding.txtSelected.setText("SELECTED : " + selected);
        getStudents();
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.cardSelectAll:
                if (binding.txtSelectDeSelect.getText().equals("Select All")) {
                    // Select All Students
                    selected = selections.size();
                    binding.txtSelected.setText("SELECTED : " + selected);
                    for (int i = 0; i < selections.size(); i++) {
                        selections.set(i, true);
                    }
                    studentAdapter.notifyDataSetChanged();
                    binding.txtSelectDeSelect.setText("DeSelect All");
                } else {
                    // DeSelect All Students
                    selected = 0;
                    binding.txtSelected.setText("SELECTED : " + selected);
                    for (int i = 0; i < selections.size(); i++) {
                        selections.set(i, false);
                    }
                    studentAdapter.notifyDataSetChanged();
                    binding.txtSelectDeSelect.setText("Select All");
                }
                break;

            case R.id.cardMessage:
                if (selected != 0) {
                    // Send Message (Selected)
                    sendAllSelected();
                } else {
                    Toast.makeText(SendSMSActivity.this, "No Student Selected", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void askPermission() {
        // Sent Message Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Ask Permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE);
            }
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void sendAllSelected() {
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

            TextView txtTitle = dialog.findViewById(R.id.txtTitle);
            txtTitle.setText("To (Selected Contacts)");
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
                        for (int i = 0; i < students.size(); i++) {
                            if (selections.get(i)) {
                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(students.get(i).getFather_phone_number(), null, messageType + "\n\n" + message, null, null);

                                    // Save Message To Database
                                    SMS sms = new SMS();
                                    sms.setAddress(students.get(i).getAddress());
                                    sms.setBy_name(Preferences.getInstance(SendSMSActivity.this).getName());
                                    sms.setBy_type(Preferences.getInstance(SendSMSActivity.this).getType());
                                    sms.setDepartment(students.get(i).getDepartment());
                                    sms.setCreation(System.currentTimeMillis());
                                    sms.setName(students.get(i).getName());
                                    sms.setFather_name(students.get(i).getFather_name());
                                    sms.setMessage_text(message);
                                    sms.setSection(students.get(i).getSection());
                                    sms.setRoll_number(students.get(i).getRoll_number());
                                    sms.setFather_phone_number(students.get(i).getFather_phone_number());
                                    sms.setMessage_type(messageType);

                                    FirebaseDatabase.getInstance().getReference().child("sms")
                                            .child(Utils.getDateID())
                                            .child(students.get(i).getDepartment())
                                            .child(students.get(i).getSection().replaceAll("\\s+", ""))
                                            .child("" + sms.getCreation())
                                            .setValue(sms)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SendSMSActivity.this, "Done", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    Toast.makeText(SendSMSActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
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
        binding.recyclerStudents.addOnItemTouchListener(new RecyclerViewItemClickListener(SendSMSActivity.this, binding.recyclerStudents, new RecyclerViewItemClickListener.OnItemClickListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onItemClick(View view, int position) {
                if (selections.get(position)) {
                    selections.set(position, false);
                    selected = selected - 1;
                    binding.txtSelected.setText("SELECTED : " + selected);
                    studentAdapter.notifyDataSetChanged();
                    binding.txtSelectDeSelect.setText("Select All");
                } else {
                    Boolean shouldSelect = false;
                    for (int i = 0; i < selections.size(); i++) {
                        if (selections.get(i)) {
                            shouldSelect = true;
                        }
                    }
                    if (shouldSelect) {
                        selections.set(position, true);
                        selected = selected + 1;
                        binding.txtSelected.setText("SELECTED : " + selected);
                        studentAdapter.notifyDataSetChanged();
                        if (selections.size() == selected) {
                            binding.txtSelectDeSelect.setText("DeSelect All");
                        } else {
                            binding.txtSelectDeSelect.setText("Select All");
                        }
                    }
                }
            }

            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onLongItemClick(View view, int position) {
                if (!selections.get(position)) {
                    selections.set(position, true);
                    selected = selected + 1;
                    binding.txtSelected.setText("SELECTED : " + selected);
                    studentAdapter.notifyDataSetChanged();
                    if (selections.size() == selected) {
                        binding.txtSelectDeSelect.setText("DeSelect All");
                    } else {
                        binding.txtSelectDeSelect.setText("Select All");
                    }
                }
            }
        }));

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