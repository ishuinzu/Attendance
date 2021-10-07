package com.ishuinzu.aitattendance.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.databinding.ActivityLoginTeacherBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Teacher;

public class LoginTeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginTeacherActivity";
    private ActivityLoginTeacherBinding binding;
    private Boolean isRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);
        binding.cardLogin.setOnClickListener(this);
        binding.cardForgetPassword.setOnClickListener(this);

        isRetry = false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.cardLogin:
                // Teacher Login
                loginTeacher();
                break;

            case R.id.cardForgetPassword:
                // Forget Password
                startActivity(new Intent(LoginTeacherActivity.this, ForgetPasswordActivity.class));
                break;
        }
    }

    private void loginTeacher() {
        // Show Loading
        LoadingDialog.showLoadingDialog(LoginTeacherActivity.this);

        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(LoginTeacherActivity.this, "Email Required", Toast.LENGTH_SHORT).show();

            // Close Loading
            LoadingDialog.closeDialog();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(LoginTeacherActivity.this, "Password Required", Toast.LENGTH_SHORT).show();

            // Close Loading
            LoadingDialog.closeDialog();
            return;
        } else {
            // Try To Sign In
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (firebaseUser != null) {
                                    // Get Teacher Details
                                    FirebaseDatabase.getInstance().getReference().child("teacher").child(firebaseUser.getUid()).child("password").setValue(password);
                                    getTeacherDetails(firebaseUser);
                                } else {
                                    if (isRetry) {
                                        // Close Loading
                                        LoadingDialog.closeDialog();

                                        Toast.makeText(LoginTeacherActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        isRetry = true;
                                        loginTeacher();
                                    }
                                }
                            } else {
                                if (isRetry) {
                                    // Close Loading
                                    LoadingDialog.closeDialog();

                                    Toast.makeText(LoginTeacherActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    isRetry = true;
                                    loginTeacher();
                                }
                            }
                        }
                    });
        }
    }

    private void getTeacherDetails(FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance().getReference().child("teacher")
                .child(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                // Teacher
                                Teacher teacher = task.getResult().getValue(Teacher.class);

                                if (teacher != null) {
                                    // Preferences
                                    Preferences.getInstance(LoginTeacherActivity.this).setLoggedIn(true);
                                    Preferences.getInstance(LoginTeacherActivity.this).setTeacher(teacher);

                                    // Close Loading
                                    LoadingDialog.closeDialog();

                                    Toast.makeText(LoginTeacherActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                                    // Redirect To Teacher Dashboard
                                    startActivity(new Intent(LoginTeacherActivity.this, DashboardTeacherActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } else {
                                    // Close Loading
                                    LoadingDialog.closeDialog();

                                    Toast.makeText(LoginTeacherActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // Close Loading
                            LoadingDialog.closeDialog();

                            Toast.makeText(LoginTeacherActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}