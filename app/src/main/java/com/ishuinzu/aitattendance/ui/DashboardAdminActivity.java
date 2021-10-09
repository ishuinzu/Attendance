package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.app.GlideApp;
import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.databinding.ActivityDashboardAdminBinding;
import com.ishuinzu.aitattendance.object.Admin;

public class DashboardAdminActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DashboardAdminActivity";
    private ActivityDashboardAdminBinding binding;
    private Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Admin
        admin = Preferences.getInstance(DashboardAdminActivity.this).getAdmin();

        // Click Listener
        binding.cardManageHODs.setOnClickListener(this);
        binding.cardManageTeachers.setOnClickListener(this);
        binding.cardManageDepartments.setOnClickListener(this);
        binding.cardSendSMS.setOnClickListener(this);
        binding.cardSMSHistory.setOnClickListener(this);
        binding.cardDarkMode.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);

        // Set Details
        binding.txtName.setText(admin.getName());
        binding.txtEmail.setText(admin.getEmail());
        GlideApp.with(DashboardAdminActivity.this).load(admin.getImg_link()).error(R.drawable.img_logo_rounded).into(binding.imgProfile);
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardManageHODs:
                // Manage HODs
                startActivity(new Intent(DashboardAdminActivity.this, ManageHODsActivity.class));
                break;

            case R.id.cardManageTeachers:
                // Manage Teachers
                startActivity(new Intent(DashboardAdminActivity.this, ManageTeachersActivity.class));
                break;

            case R.id.cardManageDepartments:
                // Manage Departments
                startActivity(new Intent(DashboardAdminActivity.this, ManageDepartmentsActivity.class));
                break;

            case R.id.cardSendSMS:
                // SMS Initialization
                startActivity(new Intent(DashboardAdminActivity.this, SMSInitializationActivity.class));
                break;

            case R.id.cardSMSHistory:
                // SMS History
                startActivity(new Intent(DashboardAdminActivity.this, SMSHistoryActivity.class));
                break;

            case R.id.cardDarkMode:
                // Dark Mode / Light Mode
                if (Preferences.getInstance(DashboardAdminActivity.this).getIsDarkMode()) {
                    // Set Light Mode
                    Preferences.getInstance(DashboardAdminActivity.this).setIsDarkMode(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    // Set Dark Mode
                    Preferences.getInstance(DashboardAdminActivity.this).setIsDarkMode(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;

            case R.id.btnLogout:
                // Logout
                Dialog dialog = new Dialog(DashboardAdminActivity.this);
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
                        Preferences.getInstance(DashboardAdminActivity.this).clearPreferences();
                        FirebaseAuth.getInstance().signOut();

                        // Redirect To User Type
                        startActivity(new Intent(DashboardAdminActivity.this, UserTypeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
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
}