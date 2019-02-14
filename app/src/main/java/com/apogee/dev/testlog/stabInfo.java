/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class stabInfo extends AppCompatActivity {

    String where, titleText, whereRef, pdfTitle;
    public static List<String> cavalList = new ArrayList<>();
    private String cavalListStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stab_info);

        Bundle extras = getIntent().getExtras();
        where = extras.getString("where");

        setValues();

        ((TextView)findViewById(R.id.titleTxt)).setText(titleText);

        getList();
    }

    /**
     * Actions for toolbar menu
     */
    @Override
    //load menu file//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stab_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                gotoSelect("add");
                return true;
            case R.id.remove:
                gotoSelect("remove");
                return false;
            case R.id.print:
                new MyAsyncTask().execute();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void setValues(){
        switch (where){
            case "devant":
                titleText = "Écuries de devant";
                whereRef = "avant";
                break;
            case "derrière":
                titleText = "Écuries de derrière";
                whereRef = "arrière";
                break;
            case "poney":
                titleText = "Écurie poney";
                whereRef = "poneys";
                break;
            default:
                titleText = "Écurie privée";
                whereRef = "privée";
        }
        pdfTitle = "Rations - "+titleText.replace("É", "é");
    }

    public void getList(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("écuries").child(whereRef);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cavalList.clear();
                cavalListStr = "";


                for (int i = 0; i<dataSnapshot.getChildrenCount();i++){
                    cavalList.add(dataSnapshot.child(""+i).getValue().toString());
                }
                for (String s : cavalList){
                    cavalListStr = cavalListStr.concat(s+"\n");
                }

                ((TextView)findViewById(R.id.listText)).setText(cavalListStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void gotoSelect(String s){
        Intent intent = new Intent(this, ponySelect.class);
        intent.putExtra("whatNext", s);
        intent.putExtra("where", whereRef);
        startActivity(intent);
    }

    public String[][] rations; //= new String[getsize()][4];

    public void printRations(){
        generatePdf gen = new generatePdf();
        gen.createTablePdf(rations, pdfTitle, "rations-"+where+"-" + System.currentTimeMillis() + ".pdf", stabInfo.this);
    }

    public String[][] getRations() {
        rations = new String[cavalList.size()][4];

        for (final String name : cavalList) {
            final int index = cavalList.indexOf(name);
            rations[index][0] = name;
            System.out.println("" + index);
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myref = db.getReference("cavalerie");
            DatabaseReference nameRef = myref.child(name).child("ration");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    rations[index][1] = dataSnapshot.child("mt").getValue(String.class);
                    rations[index][2] = dataSnapshot.child("md").getValue(String.class);
                    rations[index][3] = dataSnapshot.child("s").getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            System.out.println("r : " + rations[index][1]);
        }

        return rations;
    }

    AlphaAnimation inAnimation, outAnimation;
    FrameLayout progressBarHolder;


    //AsyncTask to make sure to get values from DB
    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
            inAnimation = new AlphaAnimation(0f,1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(Void... params) {
            rations = getRations();
            SystemClock.sleep(500);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            outAnimation = new AlphaAnimation(1f,0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            printRations();
        }
    }

}
