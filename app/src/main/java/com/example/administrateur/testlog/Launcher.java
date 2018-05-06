package com.example.administrateur.testlog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_launcher_loading);

         /*
        Checks if apps has the permission to read / write to local storage
         */
        int check = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check == PackageManager.PERMISSION_GRANTED) {
            Log.i("PermissionManager", "write granted");
        } else {
            //requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024 );
        }

        int check2 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (check2 == PackageManager.PERMISSION_GRANTED){
            Log.i("PermissionManager", "read granted");
        }
        else {
            //requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1024);
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1024 );

        }

        /*
        Begin_launcher
        */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (!hasInternetConnection(this) || !isInternetWorking()) {
            setContentView(R.layout.activity_launcher);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean hasInternetConnection(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);


        final Network network = connectivityManager.getActiveNetwork();
        final NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);

        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
