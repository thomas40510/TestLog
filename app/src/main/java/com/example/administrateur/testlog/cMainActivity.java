package com.example.administrateur.testlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

        new MyAsyncTask().execute();
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

    public void gotoMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute(){
            hideButtons();
            findViewById(R.id.progressBar3).setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(Looper.myLooper()==null){
                Looper.prepare();
            }
            //updateValue();
            new DBFetch().cfetchDB();
            Log.e("DBG", "reached");
            SystemClock.sleep(1000);
            //showRenew();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (DBFetch.clist.size()!=0) {
                findViewById(R.id.progressBar3).setVisibility(View.GONE);
                showButtons();
            } else {
                new MyAsyncTask().execute();
            }
        }
    }

    public void hideButtons(){
        findViewById(R.id.viewCavalButton).setAlpha(0.5f);
        findViewById(R.id.viewCavalButton).setClickable(false);
        for (int i=2;i<5; i++){
            String buttonId = "button" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            findViewById(resID).setAlpha(0.5f);
            findViewById(resID).setClickable(false);
        }
    }
    public void showButtons(){
        findViewById(R.id.viewCavalButton).setAlpha(1f);
        findViewById(R.id.viewCavalButton).setClickable(true);
        for (int i=2;i<5; i++){
            String buttonId = "button" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            findViewById(resID).setAlpha(1f);
            findViewById(resID).setClickable(true);
        }
    }


}
