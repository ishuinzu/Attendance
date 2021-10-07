package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.databinding.ActivityUserTypeBinding;

public class UserTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UserTypeActivity";
    private ActivityUserTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);
        binding.btnConfigAdministrator.setOnClickListener(this);
        binding.btnLoginAdministrator.setOnClickListener(this);
        binding.btnSignupHOD.setOnClickListener(this);
        binding.btnLoginHOD.setOnClickListener(this);
        binding.btnSignupTeacher.setOnClickListener(this);
        binding.btnLoginTeacher.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.btnConfigAdministrator:
                // Config Administrator
                startActivity(new Intent(UserTypeActivity.this, ConfigAdministratorActivity.class));
                break;

            case R.id.btnLoginAdministrator:
                // Login Administrator
                startActivity(new Intent(UserTypeActivity.this, LoginAdministratorActivity.class));
                break;

            case R.id.btnSignupHOD:
                // Signup HOD
                startActivity(new Intent(UserTypeActivity.this, SignupHODActivity.class));
                break;

            case R.id.btnLoginHOD:
                // Login HOD
                startActivity(new Intent(UserTypeActivity.this, LoginHODActivity.class));
                break;

            case R.id.btnSignupTeacher:
                // Signup Teacher
                startActivity(new Intent(UserTypeActivity.this, SignupTeacherActivity.class));
                break;

            case R.id.btnLoginTeacher:
                // Login Teacher
                startActivity(new Intent(UserTypeActivity.this, LoginTeacherActivity.class));
                break;
        }
    }
}