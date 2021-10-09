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
import com.ishuinzu.aitattendance.databinding.ActivityDashboardHodBinding;
import com.ishuinzu.aitattendance.object.HOD;

public class DashboardHODActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DashboardHODActivity";
    private ActivityDashboardHodBinding binding;
    private HOD hod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardHodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // HOD
        hod = Preferences.getInstance(DashboardHODActivity.this).getHOD();

        // Click Listener
        binding.cardManageTeachers.setOnClickListener(this);
        binding.cardSendSMS.setOnClickListener(this);
        binding.cardSMSHistory.setOnClickListener(this);
        binding.cardDarkMode.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);

        // Set Details
        binding.txtName.setText(hod.getName());
        binding.txtEmail.setText(hod.getEmail());
        GlideApp.with(DashboardHODActivity.this).load(hod.getImg_link()).error(R.drawable.img_logo_rounded).into(binding.imgProfile);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardManageTeachers:
                if (hod.getIs_verified()) {
                    // Manage Teachers
                    startActivity(new Intent(DashboardHODActivity.this, ManageTeachersActivity.class));
                } else {
                    // Show Dialog
                    showVerificationDialog();
                }
                break;

            case R.id.cardSendSMS:
                if (hod.getIs_verified()) {
                    // SMS Initialization
                    startActivity(new Intent(DashboardHODActivity.this, SMSInitializationActivity.class));
                } else {
                    // Show Dialog
                    showVerificationDialog();
                }
                break;

            case R.id.cardSMSHistory:
                if (hod.getIs_verified()) {
                    // SMS History
                    startActivity(new Intent(DashboardHODActivity.this, SMSHistoryActivity.class));
                } else {
                    // Show Dialog
                    showVerificationDialog();
                }
                break;

            case R.id.cardDarkMode:
                // Dark Mode / Light Mode
                if (Preferences.getInstance(DashboardHODActivity.this).getIsDarkMode()) {
                    // Set Light Mode
                    Preferences.getInstance(DashboardHODActivity.this).setIsDarkMode(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    // Set Dark Mode
                    Preferences.getInstance(DashboardHODActivity.this).setIsDarkMode(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;

            case R.id.btnLogout:
                // Logout
                Dialog dialog = new Dialog(DashboardHODActivity.this);
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
                        Preferences.getInstance(DashboardHODActivity.this).clearPreferences();
                        FirebaseAuth.getInstance().signOut();

                        // Redirect To User Type
                        startActivity(new Intent(DashboardHODActivity.this, UserTypeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
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

    private void showVerificationDialog() {
        Dialog dialog = new Dialog(DashboardHODActivity.this);
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
                FirebaseDatabase.getInstance().getReference().child("hod")
                        .child(hod.getId())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null) {
                                        hod = task.getResult().getValue(HOD.class);

                                        if (hod != null) {
                                            if (hod.getIs_verified()) {
                                                txtVerificationStatus.setText("Verified");
                                                Toast.makeText(DashboardHODActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                                            } else {
                                                txtVerificationStatus.setText("Unverified");
                                                Toast.makeText(DashboardHODActivity.this, "Unverified", Toast.LENGTH_SHORT).show();
                                            }
                                            Preferences.getInstance(DashboardHODActivity.this).setHOD(hod);
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