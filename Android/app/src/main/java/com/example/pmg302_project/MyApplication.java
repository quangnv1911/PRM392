package com.example.pmg302_project;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Log.d(TAG, "Firebase initialized in MyApplication");

    }
}