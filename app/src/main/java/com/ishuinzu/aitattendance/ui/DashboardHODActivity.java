package com.ishuinzu.aitattendance.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
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
                // Manage Teachers
                startActivity(new Intent(DashboardHODActivity.this, ManageTeachersActivity.class));
                break;

            case R.id.cardSendSMS:
                // Send SMS
                startActivity(new Intent(DashboardHODActivity.this, SendSMSActivity.class));
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
}