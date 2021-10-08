package com.ishuinzu.aitattendance;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ishuinzu.aitattendance.app.Preferences;
import com.ishuinzu.aitattendance.ui.DashboardAdminActivity;
import com.ishuinzu.aitattendance.ui.DashboardHODActivity;
import com.ishuinzu.aitattendance.ui.DashboardTeacherActivity;
import com.ishuinzu.aitattendance.ui.UserTypeActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                String request = "https://api.veevotech.com/sendsms?hash=793bba3b087f696e2def123dc0f0dbc6&receivenum=923045068602&sendernum=AITRawat&textmessage=Test SMS";
//                try {
//                    URL url = new URL(request);
//                    URLConnection urlConnection = url.openConnection();
//                    HttpURLConnection httpURLConnection = null;
//
//                    if (urlConnection instanceof HttpURLConnection) {
//                        httpURLConnection = (HttpURLConnection) urlConnection;
//                    } else {
//                        Log.d(TAG, "Message  : Invalid URL");
//                        return;
//                    }
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//                    StringBuilder urlString = new StringBuilder();
//                    String current;
//                    while ((current = bufferedReader.readLine()) != null) {
//                        urlString.append(current);
//                    }
//                    Log.d(TAG, "Message  : " + urlString);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        init();
    }

    private void init() {
        Boolean isDarkMode = Preferences.getInstance(MainActivity.this).getIsDarkMode();
        Boolean isLoggedIn = Preferences.getInstance(MainActivity.this).isLoggedIn();

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