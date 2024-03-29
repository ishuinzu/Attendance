package com.ishuinzu.aitattendance.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.aitattendance.R;
import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.app.Utils;
import com.ishuinzu.aitattendance.dialog.LoadingDialog;
import com.ishuinzu.aitattendance.object.MessageType;
import com.ishuinzu.aitattendance.object.SMS;
import com.ishuinzu.aitattendance.object.Student;
import com.ishuinzu.aitattendance.ui.UpdateStudentActivity;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private Context context;
    private List<Student> students;
    private List<Boolean> selections;
    private LayoutInflater layoutInflater;
    private List<String> messageTypeNames;
    private ArrayAdapter<String> messageTypesArrayAdapter;
    private String messageType;

    public StudentAdapter(Context context, List<Boolean> selections, List<Student> students) {
        this.context = context;
        this.selections = selections;
        this.students = students;
        this.layoutInflater = LayoutInflater.from(context);
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
        return new ViewHolder(layoutInflater.inflate(R.layout.item_student, parent, false));
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtName.setText(students.get(position).getName());
        holder.txtPhoneNumber01.setText(students.get(position).getPhone_number_01());
        holder.txtPhoneNumber02.setText(students.get(position).getPhone_number_02());

        if (selections.get(position)) {
            holder.txtName.setTextColor(context.getColor(R.color.white));
            holder.txtPhoneNumber01.setTextColor(context.getColor(R.color.white));
            holder.txtPhoneNumber02.setTextColor(context.getColor(R.color.white));
            holder.txtUpdate.setTextColor(context.getColor(R.color.white));
            holder.txtDelete.setTextColor(context.getColor(R.color.white));
            holder.txtMessage.setTextColor(context.getColor(R.color.white));
            holder.cardStudent.setCardBackgroundColor(context.getColor(R.color.primary_700));
        } else {
            holder.txtName.setTextColor(context.getColor(R.color.text_color));
            holder.txtPhoneNumber01.setTextColor(context.getColor(R.color.text_color));
            holder.txtPhoneNumber02.setTextColor(context.getColor(R.color.text_color));
            holder.txtUpdate.setTextColor(context.getColor(R.color.primary_700));
            holder.txtDelete.setTextColor(context.getColor(R.color.primary_700));
            holder.txtMessage.setTextColor(context.getColor(R.color.primary_700));
            holder.cardStudent.setCardBackgroundColor(context.getColor(R.color.text_color_invert));
        }

        holder.btnUpdate.setOnClickListener(view -> {
            // Update Student
            context.startActivity(new Intent(context, UpdateStudentActivity.class).putExtra("DEPARTMENT", students.get(position).getDepartment()).putExtra("SECTION", students.get(position).getSection()).putExtra("CREATION", students.get(position).getCreation()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });
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

                FirebaseDatabase.getInstance().getReference().child("student")
                        .child(students.get(position).getDepartment())
                        .child(students.get(position).getSection().replaceAll("\\s+", ""))
                        .child("" + students.get(position).getCreation())
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
        holder.btnMessage.setOnClickListener(view -> {
            // Message Type Dialog
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_message_type);
            dialog.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView txtTitle = dialog.findViewById(R.id.txtTitle);
            txtTitle.setText("To (" + students.get(position).getPhone_number_01() + students.get(position).getPhone_number_02() + " )");
            AutoCompleteTextView selectMessageType = dialog.findViewById(R.id.selectMessageType);
            TextInputEditText edtMessage = dialog.findViewById(R.id.edtMessage);
            selectMessageType.setOnItemClickListener((adapterView, view12, i, l) -> {
                messageType = messageTypeNames.get(i);
                dialog.findViewById(R.id.edtMessageLayout).setEnabled(!messageType.equals("Select Message Type"));
            });
            (dialog.findViewById(R.id.btnCancel)).setOnClickListener(view1 -> dialog.dismiss());
            (dialog.findViewById(R.id.btnSend)).setOnClickListener(view13 -> {
                if (!messageType.equals("Select Message Type")) {
                    String message = edtMessage.getText().toString();
                    if (!message.isEmpty()) {
                        // Send SMS
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(students.get(position).getPhone_number_01(), null, messageType + "\n\n" + message, null, null);
                            smsManager.sendTextMessage(students.get(position).getPhone_number_02(), null, messageType + "\n\n" + message, null, null);

                            // Save Message To Database
                            SMS sms = new SMS();
                            sms.setAddress(students.get(position).getAddress());
                            sms.setBy_name(Preferences.getInstance(context).getName());
                            sms.setBy_type(Preferences.getInstance(context).getType());
                            sms.setDepartment(students.get(position).getDepartment());
                            sms.setCreation(System.currentTimeMillis());
                            sms.setName(students.get(position).getName());
                            sms.setFather_name(students.get(position).getFather_name());
                            sms.setMessage_text(message);
                            sms.setSection(students.get(position).getSection());
                            sms.setRoll_number(students.get(position).getRoll_number());
                            sms.setPhone_number_01(students.get(position).getPhone_number_01());
                            sms.setPhone_number_02(students.get(position).getPhone_number_02());
                            sms.setMessage_type(messageType);

                            FirebaseDatabase.getInstance().getReference().child("sms")
                                    .child(Utils.getDateID())
                                    .child(students.get(position).getDepartment())
                                    .child(students.get(position).getSection().replaceAll("\\s+", ""))
                                    .child("" + sms.getCreation())
                                    .setValue(sms)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Message Required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Message Type Required", Toast.LENGTH_SHORT).show();
                }
            });
            messageTypeNames = new ArrayList<>();
            messageTypesArrayAdapter = new ArrayAdapter<>(context, R.layout.item_drop_down, R.id.txtItem, messageTypeNames);
            selectMessageType.setAdapter(messageTypesArrayAdapter);

            FirebaseDatabase.getInstance().getReference().child("message_type")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                messageTypeNames.clear();
                                messageTypeNames.add("Select Message Type");

                                if (snapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        MessageType messageType = dataSnapshot.getValue(MessageType.class);

                                        if (messageType != null) {
                                            messageTypeNames.add(messageType.getName());
                                        }
                                    }
                                    dialog.findViewById(R.id.selectMessageTypeLayout).setEnabled(true);
                                    messageTypesArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.background_transparent));
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardStudent;
        private final TextView txtName;
        private final TextView txtPhoneNumber01;
        private final TextView txtPhoneNumber02;
        private final ImageView btnUpdate;
        private final ImageView btnDelete;
        private final ImageView btnMessage;
        private final TextView txtUpdate;
        private final TextView txtDelete;
        private final TextView txtMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardStudent = itemView.findViewById(R.id.cardStudent);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhoneNumber01 = itemView.findViewById(R.id.txtPhoneNumber01);
            txtPhoneNumber02 = itemView.findViewById(R.id.txtPhoneNumber02);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnMessage = itemView.findViewById(R.id.btnMessage);
            txtUpdate = itemView.findViewById(R.id.txtUpdate);
            txtDelete = itemView.findViewById(R.id.txtDelete);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }
}