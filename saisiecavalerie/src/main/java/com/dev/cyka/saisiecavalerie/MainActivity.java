package com.dev.cyka.saisiecavalerie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(sharedPrefs.sharedPrefs, MODE_PRIVATE);

        //updateValue();
        DBFetch fetch = new DBFetch();
        fetch.fetchDB();
        //showRenew();


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

}
