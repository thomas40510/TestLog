package com.example.administrateur.testlog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> userlist = new ArrayList<>();
    int n;
    private ArrayList<String> toRenew = new ArrayList<>();
    private String toRenewStr;
    private int nbr;
    public static SharedPreferences prefs;
    private static AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);


        StrictMode.VmPolicy.Builder polBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(polBuilder.build());

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("nextScreen", "cavaliers");
            startActivity(intent);
            finish();
        }





        n = 1;
        builder = new AlertDialog.Builder(this);

        new MyAsyncTask().execute();

        ((Button)findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSelect("decrement");
            }
        });
        ((Button)findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSelect("increment");
            }
        });


    }


    public void showRenew(){
        Log.e("DBG main@ln80", ""+DBFetch.userlist.size());
        toRenewStr = DBFetch.toRenewStr;

        if (!toRenewStr.equals("")) {
            builder.setTitle("Cartes à renouveler : ")
                    .setMessage(toRenewStr)
                    .setPositiveButton("Compris !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
    }

    private Context mContext=MainActivity.this;

    private static final int REQUEST = 112;

    public void display (View view){
        Toast.makeText(this, "J'avais dit de ne pas cliquer !!", Toast.LENGTH_SHORT).show();
        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("cavaliers");
        for (String s : DBFetch.userlist){
            ref.child(s).child("isAssigned").setValue(false);
        }
        */
    }

    public void display3 (View view){
        Intent intent = new Intent(this, userList.class);
        startActivity(intent);
    }

    public void gotoSelect(String whatsNext){
        Intent intent = new Intent(this, userSelect.class);
        intent.putExtra("whatsNext", whatsNext);
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
            findViewById(R.id.progressBar4).setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(Looper.myLooper()==null){
                Looper.prepare();
            }
            //updateValue();
            new DBFetch().fetchDB(false, true);
            Log.e("DBG", "reached");
            SystemClock.sleep(1000);
            //showRenew();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (DBFetch.userlist.size()!=0) {
                findViewById(R.id.progressBar4).setVisibility(View.GONE);
                showButtons();
                new DBFetch().fetchDB(true, true);
            }
            else {
                new MyAsyncTask().execute();
            }
        }
    }
    public void hideButtons(){
        for (int i=2;i<6; i++){
            String buttonId = "button" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            findViewById(resID).setAlpha(0.5f);
            findViewById(resID).setClickable(false);
        }
    }
    public void showButtons(){
        for (int i=2;i<6; i++){
            String buttonId = "button" + i;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            findViewById(resID).setAlpha(1f);
            findViewById(resID).setClickable(true);
        }
    }


}
