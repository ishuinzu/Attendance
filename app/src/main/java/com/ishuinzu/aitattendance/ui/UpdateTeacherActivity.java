package com.ishuinzu.aitattendance.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.app.GlideApp;
import com.ishuinzu.aitattendance.databinding.ActivityUpdateTeacherBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Teacher;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;

public class UpdateTeacherActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdateTeacherActivity";
    private ActivityUpdateTeacherBinding binding;
    private String id;
    private Uri imageURI;
    private Boolean isImageSelected;
    private Teacher teacher;
    private Boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Get Data
        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getString("ID");
        }

        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);
        binding.btnPickImage.setOnClickListener(this);
        binding.cardUpdate.setOnClickListener(this);

        imageURI = null;
        isImageSelected = false;
        isLoading = false;
        getTeacher(id);
    }

    private void getTeacher(String id) {
        // Show Loading
        LoadingDialog.showLoadingDialog(UpdateTeacherActivity.this);

        FirebaseDatabase.getInstance().getReference().child("teacher")
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            teacher = snapshot.getValue(Teacher.class);

                            if (teacher != null) {
                                binding.edtName.setText(teacher.getName());
                                binding.edtDepartment.setText(teacher.getDepartment());
                                binding.edtEmail.setText(teacher.getEmail());
                                binding.edtPassword.setText(teacher.getPassword());
                                GlideApp.with(UpdateTeacherActivity.this).load(teacher.getImg_link()).error(R.drawable.img_logo_rounded).into(binding.imgProfile);

                                // Close Loading
                                LoadingDialog.closeDialog();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Close Loading
                        LoadingDialog.closeDialog();
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCloseScreen:
                // Close Screen
                finish();
                break;

            case R.id.btnPickImage:
                // Pick Image
                pickImage();
                break;

            case R.id.cardUpdate:
                // Update Teacher
                updateData();
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pickImage() {
        if (ContextCompat.checkSelfPermission(UpdateTeacherActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(UpdateTeacherActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Dialog dialog = new Dialog(UpdateTeacherActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_image_pick);
                dialog.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                (dialog.findViewById(R.id.btnGallery)).setOnClickListener(view -> {
                    new ImagePicker.Builder(UpdateTeacherActivity.this)
                            .mode(ImagePicker.Mode.GALLERY)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .scale(600, 200)
                            .allowMultipleImages(false)
                            .build();
                    dialog.dismiss();
                });
                (dialog.findViewById(R.id.btnCamera)).setOnClickListener(view -> {
                    new ImagePicker.Builder(UpdateTeacherActivity.this)
                            .mode(ImagePicker.Mode.CAMERA)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .scale(600, 200)
                            .allowMultipleImages(false)
                            .build();
                    dialog.dismiss();
                });

                dialog.getWindow().setAttributes(layoutParams);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(UpdateTeacherActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
            }
        } else {
            ActivityCompat.requestPermissions(UpdateTeacherActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String path = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH).get(0);

                    try {
                        Uri uri = Uri.fromFile(new File(path));
                        cropImageActivityLauncher.launch(new Intent(UpdateTeacherActivity.this, CropImageActivity.class).putExtra("URI", uri));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
        if (requestCode == 201) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    ActivityResultLauncher<Intent> cropImageActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    imageURI = result.getData().getExtras().getParcelable("CROPPED_URI");
                    binding.imgProfile.setImageURI(imageURI);
                    isImageSelected = true;
                }
            }
        }
    });

    private void updateData() {
        if (isImageSelected) {
            // Show Loading
            isLoading = true;
            LoadingDialog.showLoadingDialog(UpdateTeacherActivity.this);

            // Upload Image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child("teacher").child(imageURI.getLastPathSegment());
            storageReference.putFile(imageURI)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String imgLink = uri.toString();

                                                // Update Link
                                                FirebaseDatabase.getInstance().getReference().child("teacher").child(id).child("img_link").setValue(imgLink);

                                                Toast.makeText(UpdateTeacherActivity.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();
                                                updateOtherData();
                                            }
                                        });
                            } else {
                                // Show Loading
                                LoadingDialog.closeDialog();
                            }
                        }
                    });
        } else {
            isLoading = false;
            updateOtherData();
        }
    }

    private void updateOtherData() {
        if (!isLoading) {
            LoadingDialog.showLoadingDialog(UpdateTeacherActivity.this);
        }
        String department = binding.edtDepartment.getText().toString();
        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();

        if (!department.equals(teacher.getDepartment())) {
            // Update Department
            FirebaseDatabase.getInstance().getReference().child("teacher").child(id).child("department").setValue(department);

            Toast.makeText(UpdateTeacherActivity.this, "Department Updated", Toast.LENGTH_SHORT).show();
            teacher.setDepartment(department);
        }

        if (!name.equals(teacher.getName())) {
            // Update Name
            FirebaseDatabase.getInstance().getReference().child("teacher").child(id).child("name").setValue(name);

            Toast.makeText(UpdateTeacherActivity.this, "Name Updated", Toast.LENGTH_SHORT).show();
            teacher.setName(name);
        }

        if (!email.equals(teacher.getEmail())) {
            // Update Email
            FirebaseAuth.getInstance().signInWithEmailAndPassword(teacher.getEmail(), teacher.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Firebase Current User
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {
                                // Update Email
                                user.updateEmail(email);

                                // Database
                                FirebaseDatabase.getInstance().getReference().child("teacher").child(id).child("email").setValue(email);

                                // Teacher
                                teacher.setEmail(email);

                                // Sign out
                                FirebaseAuth.getInstance().signOut();

                                if (isLoading) {
                                    // Close Loading
                                    LoadingDialog.closeDialog();
                                    isLoading = false;

                                    Toast.makeText(UpdateTeacherActivity.this, "Email Updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        } else {
                            Toast.makeText(UpdateTeacherActivity.this, "Authentication Failed. Can't Change Email Address", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (isLoading) {
                // Close Loading
                LoadingDialog.closeDialog();
                isLoading = false;
                finish();
            }
        }
    }
}