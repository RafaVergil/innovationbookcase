package utils;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class CustomApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
