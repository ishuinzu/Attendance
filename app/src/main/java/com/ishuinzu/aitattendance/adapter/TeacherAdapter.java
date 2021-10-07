package com.ishuinzu.aitattendance.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.app.GlideApp;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.Teacher;
import com.ishuinzu.aitattendance.ui.UpdateTeacherActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {
    private Context context;
    private List<Teacher> teachers;
    private LayoutInflater layoutInflater;

    public TeacherAdapter(Context context, List<Teacher> teachers, LayoutInflater layoutInflater) {
        this.context = context;
        this.teachers = teachers;
        this.layoutInflater = layoutInflater;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_teacher, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (teachers.get(position).getIs_verified()) {
            holder.btnVerify.setImageDrawable(context.getDrawable(R.drawable.btn_verify_done));
        } else {
            holder.btnVerify.setImageDrawable(context.getDrawable(R.drawable.btn_verify));
        }
        holder.cardProfile.setOnClickListener(view -> context.startActivity(new Intent(context, UpdateTeacherActivity.class).putExtra("ID", teachers.get(position).getId()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
        holder.btnUpdate.setOnClickListener(view -> context.startActivity(new Intent(context, UpdateTeacherActivity.class).putExtra("ID", teachers.get(position).getId()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
        holder.btnDelete.setOnClickListener(view -> {
            // Show Deletion Dialog
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_delete_profile);
            dialog.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            (dialog.findViewById(R.id.btnCancel)).setOnClickListener(view1 -> dialog.dismiss());
            (dialog.findViewById(R.id.btnDelete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Show Loading
                    LoadingDialog.showLoadingDialog(context);

                    // Sign out
                    FirebaseAuth.getInstance().signOut();

                    // Delete HOD Profile
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(teachers.get(position).getEmail(), teachers.get(position).getPassword())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                        if (firebaseUser != null) {
                                            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Delete Data
                                                        FirebaseDatabase.getInstance().getReference().child("teacher").child(teachers.get(position).getId()).removeValue();
                                                        Toast.makeText(context, "Profile Deleted", Toast.LENGTH_SHORT).show();

                                                        // Close Loading
                                                        LoadingDialog.closeDialog();
                                                    } else {
                                                        // Close Loading
                                                        LoadingDialog.closeDialog();

                                                        Toast.makeText(context, "Can't Delete User", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            // Close Loading
                                            LoadingDialog.closeDialog();

                                            Toast.makeText(context, "Can't Delete User", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Close Loading
                                        LoadingDialog.closeDialog();

                                        Toast.makeText(context, "Can't Delete User", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.background_transparent));
            dialog.show();
        });
        holder.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teachers.get(position).getIs_verified()) {
                    Toast.makeText(context, "Already Verified", Toast.LENGTH_SHORT).show();
                } else {
                    // Show Verification Dialog
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_dialog_verify_profile);
                    dialog.setCancelable(true);

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(dialog.getWindow().getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    (dialog.findViewById(R.id.btnCancel)).setOnClickListener(view1 -> dialog.dismiss());
                    (dialog.findViewById(R.id.btnVerify)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Show Loading
                            LoadingDialog.showLoadingDialog(context);

                            // Verify HOD
                            FirebaseDatabase.getInstance().getReference().child("teacher")
                                    .child(teachers.get(position).getId())
                                    .child("is_verified")
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Profile Verified", Toast.LENGTH_SHORT).show();

                                                // Close Loading
                                                LoadingDialog.closeDialog();
                                            }
                                        }
                                    });
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setAttributes(layoutParams);
                    dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.background_transparent));
                    dialog.show();
                }
            }
        });
        holder.txtName.setText(teachers.get(position).getName());
        holder.txtDepartment.setText(teachers.get(position).getDepartment());
        GlideApp.with(context).load(teachers.get(position).getImg_link()).error(R.drawable.img_logo_rounded).into(holder.imgProfile);
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardProfile;
        private final TextView txtName;
        private final TextView txtDepartment;
        private final CircleImageView imgProfile;
        private final ImageView btnUpdate;
        private final ImageView btnDelete;
        private final ImageView btnVerify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProfile = itemView.findViewById(R.id.cardProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtDepartment = itemView.findViewById(R.id.txtDepartment);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnVerify = itemView.findViewById(R.id.btnVerify);
        }
    }
}