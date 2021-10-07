package com.ishuinzu.aitattendance.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.ishuinzu.aitattendance.databinding.ActivityLoginAdministratorBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Admin;

public class LoginAdministratorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginAdministratorActivity";
    private ActivityLoginAdministratorBinding binding;
    private Boolean isRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginAdministratorBinding.inflate(getLayoutInflater());
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
                // Admin Login
                adminLogin();
                break;

            case R.id.cardForgetPassword:
                // Forget Password
                startActivity(new Intent(LoginAdministratorActivity.this, ForgetPasswordActivity.class));
                break;
        }
    }

    private void adminLogin() {
        // Show Loading
        LoadingDialog.showLoadingDialog(LoginAdministratorActivity.this);

        // Check Administrator
        FirebaseDatabase.getInstance().getReference().child("config").child("admin").child("exists")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                boolean isExists = task.getResult().getValue(Boolean.class);

                                if (isExists) {
                                    Log.d(TAG, "ADMIN FOUND");

                                    loginAdminNow();
                                } else {
                                    Log.d(TAG, "NO ADMIN FOUND");

                                    if (isRetry) {
                                        // Close Loading
                                        LoadingDialog.closeDialog();
                                        Toast.makeText(LoginAdministratorActivity.this, "No Administrator Found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Close Loading
                                        LoadingDialog.closeDialog();

                                        isRetry = true;
                                        adminLogin();
                                    }
                                }
                            } else {
                                Log.d(TAG, "NO ADMIN FOUND");

                                if (isRetry) {
                                    // Close Loading
                                    LoadingDialog.closeDialog();
                                    Toast.makeText(LoginAdministratorActivity.this, "No Administrator Found", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Close Loading
                                    LoadingDialog.closeDialog();

                                    isRetry = true;
                                    adminLogin();
                                }
                            }
                        } else {
                            Log.d(TAG, "NO ADMIN FOUND");

                            if (isRetry) {
                                // Close Loading
                                LoadingDialog.closeDialog();
                                Toast.makeText(LoginAdministratorActivity.this, "No Administrator Found", Toast.LENGTH_SHORT).show();
                            } else {
                                // Close Loading
                                LoadingDialog.closeDialog();

                                isRetry = true;
                                adminLogin();
                            }
                        }
                    }
                });
    }

    private void loginAdminNow() {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(LoginAdministratorActivity.this, "Email Required", Toast.LENGTH_SHORT).show();

            // Close Loading
            LoadingDialog.closeDialog();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(LoginAdministratorActivity.this, "Password Required", Toast.LENGTH_SHORT).show();

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
                                    // Get Admin Details
                                    FirebaseDatabase.getInstance().getReference().child("admin").child(firebaseUser.getUid()).child("password").setValue(password);
                                    getAdminDetails(firebaseUser);
                                } else {
                                    if (isRetry) {
                                        // Close Loading
                                        LoadingDialog.closeDialog();

                                        Toast.makeText(LoginAdministratorActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        isRetry = true;
                                        loginAdminNow();
                                    }
                                }
                            } else {
                                if (isRetry) {
                                    // Close Loading
                                    LoadingDialog.closeDialog();

                                    Toast.makeText(LoginAdministratorActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    isRetry = true;
                                    loginAdminNow();
                                }
                            }
                        }
                    });
        }
    }

    private void getAdminDetails(FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance().getReference().child("admin")
                .child(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                // Admin
                                Admin admin = task.getResult().getValue(Admin.class);

                                if (admin != null) {
                                    // Preferences
                                    Preferences.getInstance(LoginAdministratorActivity.this).setLoggedIn(true);
                                    Preferences.getInstance(LoginAdministratorActivity.this).setAdmin(admin);
                                    Preferences.getInstance(LoginAdministratorActivity.this).setId(firebaseUser.getUid());

                                    // Close Loading
                                    LoadingDialog.closeDialog();

                                    Toast.makeText(LoginAdministratorActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                                    // Redirect To Administrator Dashboard
                                    startActivity(new Intent(LoginAdministratorActivity.this, DashboardAdminActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } else {
                                    // Close Loading
                                    LoadingDialog.closeDialog();

                                    Toast.makeText(LoginAdministratorActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // Close Loading
                            LoadingDialog.closeDialog();

                            Toast.makeText(LoginAdministratorActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}