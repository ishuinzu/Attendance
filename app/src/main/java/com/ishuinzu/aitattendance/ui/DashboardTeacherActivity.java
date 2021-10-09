package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.app.GlideApp;
import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.databinding.ActivityDashboardTeacherBinding;
import com.ishuinzu.aitattendance.object.Teacher;

public class DashboardTeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DashboardTeacherActivity";
    private ActivityDashboardTeacherBinding binding;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Teacher
        teacher = Preferences.getInstance(DashboardTeacherActivity.this).getTeacher();

        // Click Listener
        binding.cardSendSMS.setOnClickListener(this);
        binding.cardSMSHistory.setOnClickListener(this);
        binding.cardDarkMode.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);

        // Set Details
        binding.txtName.setText(teacher.getName());
        binding.txtEmail.setText(teacher.getEmail());
        GlideApp.with(DashboardTeacherActivity.this).load(teacher.getImg_link()).error(R.drawable.img_logo_rounded).into(binding.imgProfile);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardSendSMS:
                if (teacher.getIs_verified()) {
                    // SMS Initialization
                    startActivity(new Intent(DashboardTeacherActivity.this, SMSInitializationActivity.class));
                } else {
                    // Show Dialog
                    showConfirmationDialog();
                }
                break;

            case R.id.cardSMSHistory:
                if (teacher.getIs_verified()) {
                    // SMS History
                    startActivity(new Intent(DashboardTeacherActivity.this, SMSHistoryActivity.class));
                } else {
                    // Show Dialog
                    showConfirmationDialog();
                }
                break;

            case R.id.cardDarkMode:
                // Dark Mode / Light Mode
                if (Preferences.getInstance(DashboardTeacherActivity.this).getIsDarkMode()) {
                    // Set Light Mode
                    Preferences.getInstance(DashboardTeacherActivity.this).setIsDarkMode(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    // Set Dark Mode
                    Preferences.getInstance(DashboardTeacherActivity.this).setIsDarkMode(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;

            case R.id.btnLogout:
                // Logout
                Dialog dialog = new Dialog(DashboardTeacherActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_logout);
                dialog.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                (dialog.findViewById(R.id.btnCancel)).setOnClickListener(view1 -> dialog.dismiss());
                (dialog.findViewById(R.id.btnLogout)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Sign Out
                        Preferences.getInstance(DashboardTeacherActivity.this).clearPreferences();
                        FirebaseAuth.getInstance().signOut();

                        // Redirect To User Type
                        startActivity(new Intent(DashboardTeacherActivity.this, UserTypeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setAttributes(layoutParams);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
                dialog.show();
                break;
        }
    }

    private void showConfirmationDialog() {
        Dialog dialog = new Dialog(DashboardTeacherActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_verification);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView txtVerificationStatus = dialog.findViewById(R.id.txtVerificationStatus);
        (dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.btnCheckNow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check Verification Status
                FirebaseDatabase.getInstance().getReference().child("teacher")
                        .child(teacher.getId())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        teacher = task.getResult().getValue(Teacher.class);

                                        if (teacher != null) {
                                            if (teacher.getIs_verified()) {
                                                txtVerificationStatus.setText("Verified");
                                                Toast.makeText(DashboardTeacherActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                                            } else {
                                                txtVerificationStatus.setText("Unverified");
                                                Toast.makeText(DashboardTeacherActivity.this, "Unverified", Toast.LENGTH_SHORT).show();
                                            }
                                            Preferences.getInstance(DashboardTeacherActivity.this).setTeacher(teacher);
                                            dialog.dismiss();
                                        }
                                    }
                                }
                            }
                        });
            }
        });

        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
        dialog.show();
    }
}