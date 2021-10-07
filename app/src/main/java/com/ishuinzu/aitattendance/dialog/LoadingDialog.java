package com.ishuinzu.aitattendance.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.ishuinzu.aitattendance.R;

public class LoadingDialog {
    private static Dialog dialog;
    private static DisplayMetrics displayMetrics;
    private static WindowManager.LayoutParams layoutParams;

    //Get Screen Width
    public static int getScreenWidth(Context context) {
        displayMetrics = new DisplayMetrics();
        LoadingDialog.getActivity(context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    //Get Screen Height
    public static int getScreenHeight(Context context) {
        displayMetrics = new DisplayMetrics();
        LoadingDialog.getActivity(context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    //Create Loading Dialog
    public static void showLoadingDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_loading);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(DialogInterface::dismiss);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_transparent);
    }

    //Dismiss Dialog
    public static void closeDialog() {
        dialog.dismiss();
    }

    //Convert Context To Activity
    private static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity((ContextWrapper) context);
            }
        }
        return null;
    }
}