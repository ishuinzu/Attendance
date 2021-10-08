package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.adapter.SMSAdapter;
import com.ishuinzu.aitattendance.app.Utils;
import com.ishuinzu.aitattendance.databinding.ActivitySmsHistoryBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.SMS;

import java.util.ArrayList;
import java.util.List;

public class SMSHistoryActivity extends AppCompatActivity {
    private static final String TAG = "SMSHistoryActivity";
    private ActivitySmsHistoryBinding binding;
    private String dateID;
    private List<SMS> smsList;
    private SMSAdapter smsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySmsHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        dateID = Utils.getDateID();
        getMessages(dateID);

        // Calendar
        binding.calendarView.setOnDateChangeListener((calendarView1, i, i1, i2) -> {
            dateID = Utils.getValueInDoubleFigure(i1 + 1) + Utils.getValueInDoubleFigure(i2) + Utils.getValueInDoubleFigure(i);
            getMessagesDateWise(dateID);
        });
    }

    private void getMessages(String dateID) {
        smsList = new ArrayList<>();
        smsAdapter = new SMSAdapter(SMSHistoryActivity.this, smsList, dateID);
        binding.recyclerSentSMS.setAdapter(smsAdapter);

        getMessagesDateWise(dateID);
    }

    private void getMessagesDateWise(String dateID) {
        // Show Loading
        LoadingDialog.showLoadingDialog(SMSHistoryActivity.this);

        FirebaseDatabase.getInstance().getReference().child("sms")
                .child(dateID)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        smsList.clear();

                        if (snapshot.exists()) {
                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot department : snapshot.getChildren()) {
                                    if (department.getChildrenCount() != 0) {
                                        for (DataSnapshot section : department.getChildren()) {
                                            if (section.getChildrenCount() != 0) {
                                                for (DataSnapshot sms : section.getChildren()) {
                                                    SMS smsNo = sms.getValue(SMS.class);

                                                    if (smsNo != null) {
                                                        smsList.add(smsNo);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        smsAdapter.setDateID(dateID);
                        smsAdapter.notifyDataSetChanged();
                        if (smsList.size() == 0) {
                            Toast.makeText(SMSHistoryActivity.this, "No SMS Sent On " + dateID.substring(0, 2) + "/" + dateID.substring(2, 4) + "/" + dateID.substring(4), Toast.LENGTH_SHORT).show();
                        }

                        // Close Loading
                        LoadingDialog.closeDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Close Loading
                        LoadingDialog.closeDialog();
                    }
                });
    }
}