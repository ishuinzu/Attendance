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
import com.ishuinzu.aitattendance.databinding.ActivityUpdateHodBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.HOD;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;

public class UpdateHODActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdateHODActivity";
    private ActivityUpdateHodBinding binding;
    private String id;
    private Uri imageURI;
    private Boolean isImageSelected;
    private HOD hod;
    private Boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateHodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
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
        getHOD(id);
    }

    private void getHOD(String id) {
        // Show Loading
        LoadingDialog.showLoadingDialog(UpdateHODActivity.this);

        FirebaseDatabase.getInstance().getReference().child("hod")
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            hod = snapshot.getValue(HOD.class);

                            if (hod != null) {
                                binding.edtName.setText(hod.getName());
                                binding.edtDepartment.setText(hod.getDepartment());
                                binding.edtEmail.setText(hod.getEmail());
                                binding.edtPassword.setText(hod.getPassword());
                                GlideApp.with(UpdateHODActivity.this).load(hod.getImg_link()).error(R.drawable.img_logo_rounded).into(binding.imgProfile);

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
                // Update HOD
                updateData();
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pickImage() {
        if (ContextCompat.checkSelfPermission(UpdateHODActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(UpdateHODActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Dialog dialog = new Dialog(UpdateHODActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_image_pick);
                dialog.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                (dialog.findViewById(R.id.btnGallery)).setOnClickListener(view -> {
                    new ImagePicker.Builder(UpdateHODActivity.this)
                            .mode(ImagePicker.Mode.GALLERY)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .scale(600, 200)
                            .allowMultipleImages(false)
                            .build();
                    dialog.dismiss();
                });
                (dialog.findViewById(R.id.btnCamera)).setOnClickListener(view -> {
                    new ImagePicker.Builder(UpdateHODActivity.this)
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
                ActivityCompat.requestPermissions(UpdateHODActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
            }
        } else {
            ActivityCompat.requestPermissions(UpdateHODActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
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
                        cropImageActivityLauncher.launch(new Intent(UpdateHODActivity.this, CropImageActivity.class).putExtra("URI", uri));
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
            LoadingDialog.showLoadingDialog(UpdateHODActivity.this);

            // Upload Image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child("hod").child(imageURI.getLastPathSegment());
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
                                                FirebaseDatabase.getInstance().getReference().child("hod").child(id).child("img_link").setValue(imgLink);
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
            LoadingDialog.showLoadingDialog(UpdateHODActivity.this);
        }
        String department = binding.edtDepartment.getText().toString();
        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();

        if (!department.equals(hod.getDepartment())) {
            // Update Department
            FirebaseDatabase.getInstance().getReference().child("hod").child(id).child("department").setValue(department);
            hod.setDepartment(department);
        }

        if (!name.equals(hod.getName())) {
            // Update Name
            FirebaseDatabase.getInstance().getReference().child("hod").child(id).child("name").setValue(name);
            hod.setName(name);
        }

        if (!email.equals(hod.getEmail())) {
            // Update Email
            FirebaseAuth.getInstance().signInWithEmailAndPassword(hod.getEmail(), hod.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Firebase Current User
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {
                                // Update Email
                                user.updateEmail(email);

                                // Sign out
                                FirebaseAuth.getInstance().signOut();

                                // Database
                                FirebaseDatabase.getInstance().getReference().child("hod").child(id).child("email").setValue(email);

                                // HOD
                                hod.setEmail(email);

                                if (isLoading) {
                                    // Close Loading
                                    LoadingDialog.closeDialog();
                                    isLoading = false;
                                }
                            }
                        } else {
                            Toast.makeText(UpdateHODActivity.this, "Authentication Failed. Can't Change Email Address", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (isLoading) {
                // Close Loading
                LoadingDialog.closeDialog();
                isLoading = false;
            }
        }
    }
}