package com.example.administrateur.testlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class cMainActivity extends AppCompatActivity {

    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_c_main);

        prefs = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);

        //updateValue();
        DBFetch fetch = new DBFetch();
        fetch.cfetchDB();
        //showRenew();
        ((TextView) findViewById(R.id.versionName)).setText(BuildConfig.VERSION_NAME);

        final TextView webstatus  = (TextView) findViewById(R.id.webstatus);

        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        handler.postDelayed(new Runnable(){
            public void run(){
                webstatus.setText("fetching...");
                if (!hasInternetConnection(getApplicationContext())){
                    webstatus.setText("Not working !");
                }
                else{
                    webstatus.setText("fine ;)");
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }


    public void showCaval(View view){
        Intent intent = new Intent(this, ponyList.class);
        startActivity(intent);
    }
    public void newHorse(View view){
        Intent intent = new Intent(this, NewPony.class);
        startActivity(intent);
    }
    public void newVerm(View view){
        Intent intent = new Intent(this, AddVerm.class);
        startActivity(intent);
    }
    public void addvac(View view){
        Intent intent = new Intent(this, addVac.class);
        startActivity(intent);
    }

    /**
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
     */

    public static boolean hasInternetConnection(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
        /*
        final Network network = connectivityManager.getActiveNetwork();
        final NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);

        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    */
    }


}
