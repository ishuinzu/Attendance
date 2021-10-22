package com.ishuinzu.aitattendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.ui.DashboardAdminActivity;
import com.ishuinzu.aitattendance.ui.DashboardHODActivity;
import com.ishuinzu.aitattendance.ui.DashboardTeacherActivity;
import com.ishuinzu.aitattendance.ui.UserTypeActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        boolean isDarkMode = Preferences.getInstance(MainActivity.this).getIsDarkMode();
        boolean isLoggedIn = Preferences.getInstance(MainActivity.this).isLoggedIn();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (isLoggedIn) {
            String type = Preferences.getInstance(MainActivity.this).getType();

            switch (type) {
                case "ADMIN":
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this, DashboardAdminActivity.class));
                        finish();
                    }, DELAY);
                    break;
                case "HOD":
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this, DashboardHODActivity.class));
                        finish();
                    }, DELAY);
                    break;
                case "TEACHER":
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this, DashboardTeacherActivity.class));
                        finish();
                    }, DELAY);
                    break;
                default:
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(MainActivity.this, UserTypeActivity.class));
                        finish();
                    }, DELAY);
            }
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, UserTypeActivity.class));
                finish();
            }, DELAY);
        }
    }
}