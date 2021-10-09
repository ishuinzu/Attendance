package com.ishuinzu.aitattendance.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.SMS;

import java.util.Date;
import java.util.List;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.ViewHolder> {
    private Context context;
    private List<SMS> smsList;
    private String dateID;
    private LayoutInflater layoutInflater;

    public SMSAdapter(Context context, List<SMS> smsList, String dateID) {
        this.context = context;
        this.smsList = smsList;
        this.layoutInflater = LayoutInflater.from(context);
        this.dateID = dateID;
        setHasStableIds(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDateID(String dateID) {
        this.dateID = dateID;
        notifyDataSetChanged();
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
        return new ViewHolder(layoutInflater.inflate(R.layout.item_sent_sms, parent, false));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtName.setText(smsList.get(position).getName());
        holder.txtPhoneNumber.setText(smsList.get(position).getFather_phone_number());
        holder.txtDepartment.setText("Department : " + smsList.get(position).getDepartment());
        holder.txtSection.setText("Section : " + smsList.get(position).getSection());
        holder.txtBy.setText("By : " + smsList.get(position).getBy_name() + " (" + smsList.get(position).getBy_type() + ")");
        holder.txtType.setText("Type : " + smsList.get(position).getMessage_type());
        holder.txtMessage.setText("Message : " + smsList.get(position).getMessage_text());
        holder.txtDateTime.setText("Date - Time : " + new Date(smsList.get(position).getCreation()).toString().substring(0, 19));
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
            (dialog.findViewById(R.id.btnDelete)).setOnClickListener(view14 -> {
                // Show Loading
                LoadingDialog.showLoadingDialog(context);

                FirebaseDatabase.getInstance().getReference().child("sms")
                        .child(dateID)
                        .child(smsList.get(position).getDepartment())
                        .child(smsList.get(position).getSection().replaceAll("\\s+", ""))
                        .child("" + smsList.get(position).getCreation())
                        .removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Close Loading
                                LoadingDialog.closeDialog();

                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            });

            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.background_transparent));
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardStudent;
        private final TextView txtName;
        private final TextView txtPhoneNumber;
        private final TextView txtBy;
        private final TextView txtType;
        private final TextView txtDateTime;
        private final TextView txtDepartment;
        private final TextView txtSection;
        private final TextView txtMessage;
        private final ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardStudent = itemView.findViewById(R.id.cardStudent);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhoneNumber = itemView.findViewById(R.id.txtPhoneNumber);
            txtType = itemView.findViewById(R.id.txtType);
            txtBy = itemView.findViewById(R.id.txtBy);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtDepartment = itemView.findViewById(R.id.txtDepartment);
            txtSection = itemView.findViewById(R.id.txtSection);
        }
    }
}