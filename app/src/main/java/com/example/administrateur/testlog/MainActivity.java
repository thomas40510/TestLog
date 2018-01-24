package com.example.administrateur.testlog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

        StrictMode.VmPolicy.Builder polBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(polBuilder.build());

        prefs = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);

        n = 1;
        builder = new AlertDialog.Builder(this);

        //updateValue();
        DBFetch fetch = new DBFetch();
        fetch.fetchDB();
        //showRenew();


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
        //Intent intent = new Intent(this, editDB.class);
        //startActivity(intent);

        String test = "";

        for (int i = 0; i<DBFetch.userlist.size(); i++){
            test = test.concat(DBFetch.userlist.get(i)+"\n");
        }

        generatePdf gen = new generatePdf();
        gen.createPdf(test, "Users", "file.pdf", this);

        //createPdf(test, "Users");

    }

    public void display3 (View view){
        Intent intent = new Intent(this, userList.class);
        startActivity(intent);
    }

    public void gotoSelect(View view){
        Intent intent = new Intent(this, userSelect.class);
        startActivity(intent);
    }
    public void gotoAuth (View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
