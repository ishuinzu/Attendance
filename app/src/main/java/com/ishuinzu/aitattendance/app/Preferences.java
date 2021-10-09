package com.ishuinzu.aitattendance.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.ishuinzu.aitattendance.object.Admin;
import com.ishuinzu.aitattendance.object.HOD;
import com.ishuinzu.aitattendance.object.Teacher;

public class Preferences {
    private static Preferences PREFERENCES;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREFERENCES_NAME = "PREFERENCES";
    private static final String IS_DARK_MODE = "IS_DARK_MODE";
    private static final String LOGGED_IN = "LOGGED_IN";
    private static final String TYPE = "TYPE";
    private static final String ID = "ID";
    private static final String ADMIN_CREATION = "ADMIN_CREATION";
    private static final String ADMIN_EMAIL = "ADMIN_EMAIL";
    private static final String ADMIN_ID = "ADMIN_ID";
    private static final String ADMIN_IMAGE_LINK = "ADMIN_IMAGE_LINK";
    private static final String ADMIN_NAME = "ADMIN_NAME";
    private static final String ADMIN_PASSWORD = "ADMIN_PASSWORD";
    private static final String HOD_CREATION = "HOD_CREATION";
    private static final String HOD_DEPARTMENT = "HOD_DEPARTMENT";
    private static final String HOD_EMAIL = "HOD_EMAIL";
    private static final String HOD_IMAGE_LINK = "HOD_IMAGE_LINK";
    private static final String HOD_IS_VERIFIED = "HOD_IS_VERIFIED";
    private static final String HOD_NAME = "HOD_NAME";
    private static final String HOD_PASSWORD = "HOD_PASSWORD";
    private static final String TEACHER_CREATION = "TEACHER_CREATION";
    private static final String TEACHER_DEPARTMENT = "TEACHER_DEPARTMENT";
    private static final String TEACHER_EMAIL = "TEACHER_EMAIL";
    private static final String TEACHER_IMAGE_LINK = "TEACHER_IMAGE_LINK";
    private static final String TEACHER_IS_VERIFIED = "TEACHER_IS_VERIFIED";
    private static final String TEACHER_NAME = "TEACHER_NAME";
    private static final String TEACHER_PASSWORD = "TEACHER_PASSWORD";
    private static final String DIALOG_INSTRUCTIONS_O1 = "DIALOG_INSTRUCTIONS_O1";

    private Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized Preferences getInstance(Context context) {
        if (PREFERENCES == null) {
            PREFERENCES = new Preferences(context);
        }
        return PREFERENCES;
    }

    public void setLoggedIn(Boolean isLoggedIn) {
        editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public Boolean isLoggedIn() {
        return sharedPreferences.getBoolean(LOGGED_IN, false);
    }

    public void setIsDarkMode(Boolean isDarkMode) {
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_DARK_MODE, isDarkMode);
        editor.apply();
    }

    public Boolean getIsDarkMode() {
        return sharedPreferences.getBoolean(IS_DARK_MODE, false);
    }

    public void setAdmin(Admin admin) {
        editor = sharedPreferences.edit();
        editor.putLong(ADMIN_CREATION, admin.getCreation());
        editor.putString(ADMIN_EMAIL, admin.getEmail());
        editor.putString(ADMIN_ID, admin.getId());
        editor.putString(ADMIN_IMAGE_LINK, admin.getImg_link());
        editor.putString(ADMIN_NAME, admin.getName());
        editor.putString(ADMIN_PASSWORD, admin.getPassword());
        editor.putString(TYPE, "ADMIN");
        editor.apply();
    }

    public Admin getAdmin() {
        return new Admin(
                sharedPreferences.getLong(ADMIN_CREATION, 0),
                sharedPreferences.getString(ADMIN_EMAIL, null),
                sharedPreferences.getString(ADMIN_ID, null),
                sharedPreferences.getString(ADMIN_IMAGE_LINK, null),
                sharedPreferences.getString(ADMIN_NAME, null),
                sharedPreferences.getString(ADMIN_PASSWORD, null)
        );
    }

    public void setHOD(HOD hod) {
        editor = sharedPreferences.edit();
        editor.putLong(HOD_CREATION, hod.getCreation());
        editor.putString(HOD_DEPARTMENT, hod.getDepartment());
        editor.putString(HOD_EMAIL, hod.getEmail());
        editor.putString(ID, hod.getId());
        editor.putString(HOD_IMAGE_LINK, hod.getImg_link());
        editor.putBoolean(HOD_IS_VERIFIED, hod.getIs_verified());
        editor.putString(HOD_NAME, hod.getName());
        editor.putString(HOD_PASSWORD, hod.getPassword());
        editor.putString(TYPE, "HOD");
        editor.apply();
    }

    public HOD getHOD() {
        return new HOD(
                sharedPreferences.getLong(HOD_CREATION, 0),
                sharedPreferences.getString(HOD_DEPARTMENT, null),
                sharedPreferences.getString(HOD_EMAIL, null),
                sharedPreferences.getString(ID, null),
                sharedPreferences.getString(HOD_IMAGE_LINK, null),
                sharedPreferences.getBoolean(HOD_IS_VERIFIED, false),
                sharedPreferences.getString(HOD_NAME, null),
                sharedPreferences.getString(HOD_PASSWORD, null)
        );
    }

    public void setTeacher(Teacher teacher) {
        editor = sharedPreferences.edit();
        editor.putLong(TEACHER_CREATION, teacher.getCreation());
        editor.putString(TEACHER_DEPARTMENT, teacher.getDepartment());
        editor.putString(TEACHER_EMAIL, teacher.getEmail());
        editor.putString(ID, teacher.getId());
        editor.putString(TEACHER_IMAGE_LINK, teacher.getImg_link());
        editor.putBoolean(TEACHER_IS_VERIFIED, teacher.getIs_verified());
        editor.putString(TEACHER_NAME, teacher.getName());
        editor.putString(TEACHER_PASSWORD, teacher.getPassword());
        editor.putString(TYPE, "TEACHER");
        editor.apply();
    }

    public Teacher getTeacher() {
        return new Teacher(
                sharedPreferences.getLong(TEACHER_CREATION, 0),
                sharedPreferences.getString(TEACHER_DEPARTMENT, null),
                sharedPreferences.getString(TEACHER_EMAIL, null),
                sharedPreferences.getString(ID, null),
                sharedPreferences.getString(TEACHER_IMAGE_LINK, null),
                sharedPreferences.getBoolean(TEACHER_IS_VERIFIED, false),
                sharedPreferences.getString(TEACHER_NAME, null),
                sharedPreferences.getString(TEACHER_PASSWORD, null)
        );
    }

    public void setId(String id) {
        editor = sharedPreferences.edit();
        editor.putString(ID, id);
        editor.apply();
    }

    public String getId() {
        return sharedPreferences.getString(ID, null);
    }

    public String getType() {
        return sharedPreferences.getString(TYPE, null);
    }

    public String getName() {
        switch (getType()) {
            case "ADMIN":
                return sharedPreferences.getString(ADMIN_NAME, null);
            case "HOD":
                return sharedPreferences.getString(HOD_NAME, null);
            case "TEACHER":
                return sharedPreferences.getString(TEACHER_NAME, null);
        }
        return null;
    }

    public String getHODDepartment() {
        return sharedPreferences.getString(HOD_DEPARTMENT, null);
    }

    public void clearPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    public Boolean getDialogInstructions01() {
        return sharedPreferences.getBoolean(DIALOG_INSTRUCTIONS_O1, false);
    }

    public void setDialogInstructionsO1(Boolean isDoNotShowAgain) {
        editor = sharedPreferences.edit();
        editor.putBoolean(DIALOG_INSTRUCTIONS_O1, isDoNotShowAgain);
        editor.apply();
    }

    public String getEmail() {
        switch (getType()) {
            case "ADMIN":
                return sharedPreferences.getString(ADMIN_EMAIL, null);
            case "HOD":
                return sharedPreferences.getString(HOD_EMAIL, null);
            case "TEACHER":
                return sharedPreferences.getString(TEACHER_EMAIL, null);
        }
        return null;
    }

    public String getPassword() {
        switch (getType()) {
            case "ADMIN":
                return sharedPreferences.getString(ADMIN_PASSWORD, null);
            case "HOD":
                return sharedPreferences.getString(HOD_PASSWORD, null);
            case "TEACHER":
                return sharedPreferences.getString(TEACHER_PASSWORD, null);
        }
        return null;
    }
}