/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class repInfo extends AppCompatActivity {

    public static String day, reprise;

    public static List<String> cavalList = new ArrayList<>();
    private String cavalListStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_info);

        Bundle extras = getIntent().getExtras();
        day = extras.getString("day");
        reprise = extras.getString("reprise");

        ((TextView)findViewById(R.id.titleTxt)).setText(day+" - cours de "+ reprise.replace(":", "h"));

        getCavalListStr();
    }

    /**
     * Actions for toolbar menu
     */
    @Override
    //load menu file//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rep_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addUser:
                gotoSelect("addUser");
                return true;
            case R.id.delUser:
                gotoSelect("delUser");
                return false;
            case R.id.decrement:
                gotoSelect("decrementlist");
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getCavalListStr(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("reprises");
        DatabaseReference repRef = myref.child(day).child(reprise);

        repRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cavalList.clear();
                cavalListStr = "";
                ((TextView)findViewById(R.id.txtNiveau)).setText(dataSnapshot.child("niveau").getValue().toString());

                for (int i = 0; i<dataSnapshot.child("cavaliers").getChildrenCount();i++){
                    cavalList.add(dataSnapshot.child("cavaliers").child(""+i).getValue().toString());
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
        return cavalListStr;
    }


    public void gotoSelect(String whatsNext){
        Intent intent = new Intent(this, userSelect.class);
        intent.putExtra("whatsNext", whatsNext);
        startActivity(intent);
    }
}
