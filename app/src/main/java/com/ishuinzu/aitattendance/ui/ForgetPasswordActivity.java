package com.ishuinzu.aitattendance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.databinding.ActivityForgetPasswordBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ForgetPasswordActivity";
    private ActivityForgetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);
        binding.cardSendLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.cardSendLink:
                // Send Link
                sendLink();
                break;
        }
    }

    private void sendLink() {
        String email = binding.edtEmail.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(ForgetPasswordActivity.this, "Email Required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // Show Loading
            LoadingDialog.showLoadingDialog(ForgetPasswordActivity.this);

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Close Dialog
                            LoadingDialog.closeDialog();

                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPasswordActivity.this, "Link Send. Please Check Your Email", Toast.LENGTH_SHORT).show();

                                // Redirect To User Type
                                startActivity(new Intent(ForgetPasswordActivity.this, UserTypeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}