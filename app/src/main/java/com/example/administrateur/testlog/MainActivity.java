package com.example.administrateur.testlog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        n = 1;
        builder = new AlertDialog.Builder(this);

        //updateValue();
        DBFetch fetch = new DBFetch();
        fetch.fetchDB();
        //showRenew();


    }

    public void updateValue () {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String user = postsnapshot.getKey();
                    userlist.add(user);
                    if (Integer.parseInt(dataSnapshot.child(user).child("remainH").getValue().toString())<=0){
                        toRenewStr = toRenewStr.concat(user);
                    }
                }
                Log.d("INFO", userlist.toString());
                //updateText(1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public void showRenew(){
        Log.e("DBG main@ln80", ""+DBFetch.userlist.size());
        toRenewStr = DBFetch.toRenewStr;

        if (!toRenewStr.equals("")) {
            builder.setTitle("to renew : ")
                    .setMessage(toRenewStr)
                    .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
    }

    public void display (View view){
        Intent intent = new Intent(this, editDB.class);
        startActivity(intent);

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
