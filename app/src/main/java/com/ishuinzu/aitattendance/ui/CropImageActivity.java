package com.ishuinzu.aitattendance.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ishuinzu.aitattendance.databinding.ActivityCropImageBinding;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;

import java.io.ByteArrayOutputStream;

public class CropImageActivity extends AppCompatActivity {
    private static final String TAG = "CropImageActivity";
    private ActivityCropImageBinding binding;
    private Uri sourceURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Image URI
        if (getIntent().getExtras() != null) {
            sourceURI = getIntent().getExtras().getParcelable("URI");
        }

        // Set URI
        binding.cropImageView.setImageURI(sourceURI);
        binding.cropImageView.setInitialFrameScale(0.5f);
        binding.cropImageView.setCropMode(CropImageView.CropMode.SQUARE);

        // Click Listener
        binding.btnCropImage.setOnClickListener(view -> binding.cropImageView.crop(sourceURI).execute(new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                setResult(RESULT_OK, new Intent().putExtra("CROPPED_URI", getImageURI(cropped)));
                finish();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "ERROR " + e.toString());
            }
        }));
    }

    // Bitmap To URI
    public Uri getImageURI(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Cropped_Image_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }
}