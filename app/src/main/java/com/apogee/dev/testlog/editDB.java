/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class editDB extends AppCompatActivity {
    List<String> userlist = new ArrayList<>();
    public String newDate;
    List<String> licList = new ArrayList<>();
    List<String> dateArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_db);
    }

    public void onClick(View view){
        userlist = DBFetch.userlist;
    }
    /**public void onClick(View view){
        userlist = DBFetch.userlist;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mref = database.getReference("licences");
        licList.clear();
        for (int i = 0; i<userlist.size(); i++) {
            DatabaseReference reference = mref.child(userlist.get(i));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String lNbr = snapshot.getValue().toString();
                        licList.add(lNbr);
                    }
                    Log.e("DBG editDB@ln58", licList.toString());
                    Log.e("DBG editDB @ln59", "" + userlist.size() + " / " + licList.size());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }*/
     /** /!\ Used for bDates formatting in DB, don't use till needed again (DANGER !!)/!\

    public void edit(List<String> userlist){
        for (String s : userlist) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myref = database.getReference("cavaliers");
            DatabaseReference ref = myref.child(s);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String dateStr = dataSnapshot.child("Birthdate").getValue(String.class);
                    char[] charArray1 = new char[2];
                    char[] charArray2 = new char[2];
                    char[] charArray3 = new char[4];
                    dateStr.getChars(6,8,charArray1,0);
                    dateStr.getChars(4,6,charArray2, 0);
                    dateStr.getChars(0,4,charArray3, 0);
                    newDate = new StringBuilder().append(charArray1).append("/").append(charArray2).append("/").append(charArray3).toString();
                    dateArray.add(newDate);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            Log.e("INFO", dateArray.toString()+dateArray.size());
        }
        for (int i=0; i<dateArray.size(); i++){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myref = database.getReference("cavaliers");
            DatabaseReference ref = myref.child(userlist.get(i));
            ref.child("Birthdate").setValue(dateArray.get(i));
        }

    }
    */
     /*
    public void edit(List<String> userlist){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavaliers");
        for (String s : userlist) {
            DatabaseReference ref = myref.child(s).child("mail");
            ref.setValue("none");
        }
        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
    }*/
}
