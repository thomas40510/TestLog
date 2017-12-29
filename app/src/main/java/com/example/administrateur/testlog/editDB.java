package com.example.administrateur.testlog;

import android.os.Bundle;
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

public class editDB extends AppCompatActivity {
    List<String> userlist = new ArrayList<>();
    public String newDate;
    List<String> dateArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_db);
    }
    public void onClick(View view){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot postsnapshot: dataSnapshot.getChildren()){
                    String user = postsnapshot.getKey();
                    userlist.add(user);
                }
                Log.d("INFO", userlist.toString()+userlist.size());

                for (String s : userlist){
                    DatabaseReference reference = database.getReference("users");
                    DatabaseReference mref = reference.child(s);
                    mref.child("remainH").setValue("42");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
     /* //!\ Used for bDates formatting in DB, don't use till needed again (DANGER)/!\

    public void edit(List<String> userlist){
        for (String s : userlist) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myref = database.getReference("users");
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
            DatabaseReference myref = database.getReference("users");
            DatabaseReference ref = myref.child(userlist.get(i));
            ref.child("Birthdate").setValue(dateArray.get(i));
        }

    }
    */
}
