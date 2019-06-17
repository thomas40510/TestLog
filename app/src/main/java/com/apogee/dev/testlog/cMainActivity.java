/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;

import io.fabric.sdk.android.Fabric;

public class cMainActivity extends AppCompatActivity {

    public static SharedPreferences prefs;

    //TODO : registre entrées / sorties

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_c_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("nextScreen", "cavalerie");
            startActivity(intent);
            finish();
        }

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
        Intent intent = new Intent(this, ponySelect.class);
        intent.putExtra("whatNext", "vermifuge")
                .putExtra("where", "");
        startActivity(intent);
    }
    public void addvac(View view){
        Intent intent = new Intent(this, ponySelect.class);
        intent.putExtra("whatNext", "vaccin")
                .putExtra("where", "");
        startActivity(intent);
    }
    public void addnico(View view){
        Intent intent = new Intent(this, ponySelect.class);
        intent.putExtra("whatNext", "nico")
                .putExtra("where", "");
        startActivity(intent);
    }
    public void addost(View view){
        Intent intent = new Intent(this, ponySelect.class);
        intent.putExtra("whatNext", "ostéo")
                .putExtra("where", "");
        startActivity(intent);
    }
    public void adddent(View view){
        Intent intent = new Intent(this, ponySelect.class);
        intent.putExtra("whatNext", "dentiste")
                .putExtra("where", "");
        startActivity(intent);
    }
    public void vacbymonth(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Entrez le mois")
                .setMessage("Choisissez le mois à afficher :");

        final NumberPicker numberPicker = new NumberPicker(cMainActivity.this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);
        builder.setView(numberPicker);

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int month = numberPicker.getValue();
                Intent intent = new Intent(cMainActivity.this, showVV.class);
                intent.putExtra("name", "none");
                intent.putExtra("type", "vac");
                intent.putExtra("month", Integer.toString(month));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
    public void vermbymonth(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Entrez le mois")
                .setMessage("Choisissez le mois à afficher :");

        final NumberPicker numberPicker = new NumberPicker(cMainActivity.this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);
        builder.setView(numberPicker);

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int month = numberPicker.getValue();
                Intent intent = new Intent(cMainActivity.this, showVV.class);
                intent.putExtra("name", "none");
                intent.putExtra("type", "verm");
                intent.putExtra("month", Integer.toString(month));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void gotoMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void editDB (View view){


        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("cavalerie");

        for (String s : DBFetch.clist){
            ref.child(s).child("lastverm").setValue(null);
            ref.child(s).child("lastvacs").setValue(null);
        }
        */


        Toast.makeText(this, "J'avais dit de ne pas appuyer !", Toast.LENGTH_SHORT).show();
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
            new DBFetch().cfetchDB(true);
            //Log.e("DBG", "reached");
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
        for (int i=401;i<413; i++){
            String buttonId = "button" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            findViewById(resID).setAlpha(0.5f);
            findViewById(resID).setClickable(false);
        }
    }
    public void showButtons(){
        for (int i=401;i<413; i++){
            String buttonId = "button" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            findViewById(resID).setAlpha(1f);
            findViewById(resID).setClickable(true);
        }
    }


}
