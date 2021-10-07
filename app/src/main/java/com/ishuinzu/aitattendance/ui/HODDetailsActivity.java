package com.ishuinzu.aitattendance.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.databinding.ActivityHodDetailsBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.HOD;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;

public class HODDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HODDetailsActivity";
    private ActivityHodDetailsBinding binding;
    private String departmentName;
    private Uri imageURI;
    private Boolean isImageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHodDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);
        binding.btnPickImage.setOnClickListener(this);
        binding.cardSaveConfiguration.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            departmentName = getIntent().getExtras().getString("DEPARTMENT");
            binding.edtDepartment.setText(departmentName);
        }
        imageURI = null;
        isImageSelected = false;
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

            case R.id.cardSaveConfiguration:
                // Signup HOD
                signupHOD();
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pickImage() {
        if (ContextCompat.checkSelfPermission(HODDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(HODDetailsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Dialog dialog = new Dialog(HODDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_image_pick);
                dialog.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                (dialog.findViewById(R.id.btnGallery)).setOnClickListener(view -> {
                    new ImagePicker.Builder(HODDetailsActivity.this)
                            .mode(ImagePicker.Mode.GALLERY)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .scale(600, 200)
                            .allowMultipleImages(false)
                            .build();
                    dialog.dismiss();
                });
                (dialog.findViewById(R.id.btnCamera)).setOnClickListener(view -> {
                    new ImagePicker.Builder(HODDetailsActivity.this)
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
                ActivityCompat.requestPermissions(HODDetailsActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
            }
        } else {
            ActivityCompat.requestPermissions(HODDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
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
                        cropImageActivityLauncher.launch(new Intent(HODDetailsActivity.this, CropImageActivity.class).putExtra("URI", uri));
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

    private void signupHOD() {
        if (isImageSelected) {
            // Show Loading
            LoadingDialog.showLoadingDialog(HODDetailsActivity.this);

            // Get HOD Details
            String name = binding.edtName.getText().toString();
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPassword.getText().toString();
            String retypePassword = binding.edtRetypePassword.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(HODDetailsActivity.this, "Name Required", Toast.LENGTH_SHORT).show();

                // Close Loading
                LoadingDialog.closeDialog();
                return;
            } else if (email.isEmpty()) {
                Toast.makeText(HODDetailsActivity.this, "Email Required", Toast.LENGTH_SHORT).show();

                // Close Loading
                LoadingDialog.closeDialog();
                return;
            } else if (password.isEmpty()) {
                Toast.makeText(HODDetailsActivity.this, "Password Required", Toast.LENGTH_SHORT).show();

                // Close Loading
                LoadingDialog.closeDialog();
                return;
            } else if (retypePassword.isEmpty()) {
                Toast.makeText(HODDetailsActivity.this, "Retype Password Required", Toast.LENGTH_SHORT).show();

                // Close Loading
                LoadingDialog.closeDialog();
                return;
            } else if (!password.equals(retypePassword)) {
                Toast.makeText(HODDetailsActivity.this, "Passwords Should Be Same", Toast.LENGTH_SHORT).show();

                // Close Loading
                LoadingDialog.closeDialog();
                return;
            } else if (password.length() < 8) {
                Toast.makeText(HODDetailsActivity.this, "Minimum 08 Characters Required", Toast.LENGTH_SHORT).show();

                // Close Loading
                LoadingDialog.closeDialog();
                return;
            } else {
                // Check HOD Existence
                FirebaseDatabase.getInstance().getReference().child("config")
                        .child("hods")
                        .child("hod" + departmentName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DataSnapshot dataSnapshot = task.getResult();

                                    if (dataSnapshot != null) {
                                        if (dataSnapshot.getValue() != null) {
                                            Boolean isFounded = (Boolean) dataSnapshot.getValue();

                                            if (isFounded) {
                                                // Close Dialog
                                                LoadingDialog.closeDialog();

                                                Toast.makeText(HODDetailsActivity.this, departmentName + "'s HOD Already Exists", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Signup - Email & Password
                                                signupHODNow(email, name, password);
                                            }
                                        } else {
                                            // Signup - Email & Password
                                            signupHODNow(email, name, password);
                                        }
                                    } else {
                                        // Signup - Email & Password
                                        signupHODNow(email, name, password);
                                    }
                                } else {
                                    // Signup - Email & Password
                                    signupHODNow(email, name, password);
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(HODDetailsActivity.this, "No Profile Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void signupHODNow(String email, String name, String password) {
        // Sign - Email & Password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (firebaseUser != null) {
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

                                                                    // HOD
                                                                    HOD hod = new HOD();
                                                                    hod.setDepartment(departmentName);
                                                                    hod.setCreation(System.currentTimeMillis());
                                                                    hod.setEmail(email);
                                                                    hod.setName(name);
                                                                    hod.setImg_link(imgLink);
                                                                    hod.setIs_verified(false);
                                                                    hod.setId(firebaseUser.getUid());
                                                                    hod.setPassword(password);

                                                                    // Upload HOD
                                                                    FirebaseDatabase.getInstance().getReference().child("config").child("hods").child("hod" + departmentName).setValue(true);
                                                                    FirebaseDatabase.getInstance().getReference().child("hod")
                                                                            .child(firebaseUser.getUid())
                                                                            .setValue(hod)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Log.d(TAG, "HOD UPLOADED");
                                                                                        // Close Loading
                                                                                        LoadingDialog.closeDialog();

                                                                                        Toast.makeText(HODDetailsActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                                                                        // Redirect To User Type
                                                                                        startActivity(new Intent(HODDetailsActivity.this, UserTypeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                                                                        finish();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            } else {
                                // Close Loading
                                LoadingDialog.closeDialog();

                                Toast.makeText(HODDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Close Loading
                            LoadingDialog.closeDialog();

                            Toast.makeText(HODDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}