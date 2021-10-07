package com.ishuinzu.aitattendance.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.adapter.HODAdapter;
import com.ishuinzu.aitattendance.databinding.ActivityManageHodsBinding;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.HOD;

import java.util.ArrayList;
import java.util.List;

public class ManageHODsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ManageHODsActivity";
    private ActivityManageHodsBinding binding;
    private List<HOD> hods;
    private HODAdapter hodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageHodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // Click Listener
        binding.btnCloseScreen.setOnClickListener(this);

        getHODs();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCloseScreen) {
            // Close Screen
            finish();
        }
    }

    private void getHODs() {
        hods = new ArrayList<>();
        hodAdapter = new HODAdapter(ManageHODsActivity.this, hods, getLayoutInflater());
        binding.recyclerHODs.setAdapter(hodAdapter);

        loadHODs();
    }

    private void loadHODs() {
        // Show Loading
        LoadingDialog.showLoadingDialog(ManageHODsActivity.this);

        FirebaseDatabase.getInstance().getReference().child("hod")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hods.clear();

                        if (snapshot.exists()) {
                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    HOD hod = dataSnapshot.getValue(HOD.class);

                                    if (hod != null) {
                                        hods.add(hod);
                                    }
                                }
                                hodAdapter.notifyDataSetChanged();

                                // Close Loading
                                LoadingDialog.closeDialog();
                            }
                        } else {
                            // Close Loading
                            LoadingDialog.closeDialog();

                            Toast.makeText(ManageHODsActivity.this, "No Result Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Close Loading
                        LoadingDialog.closeDialog();
                    }
                });
    }
}