package com.example.administrateur.testlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        n = 1;
    }
    public void updateValue (View view){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot postsnapshot: dataSnapshot.getChildren()){
                    String user = postsnapshot.getKey();
                    userlist.add(user);
                }
                Log.d("INFO", userlist.toString());
                updateText(1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void updateText (int n){
         for(int i = n; i<n+10;i++ ) {
            String textid = "textView"+(i-(n-1));
            Log.d("DEBUG", ""+(i));
            int resID = getResources().getIdentifier(textid, "id", getPackageName());
            TextView text = (TextView) findViewById(resID);
            text.setText(userlist.get(i-1));
        }
    }
    public void shownext (View view){
        if (n<(userlist.size()-20)) {
            n += 10;
        } else {
            n = userlist.size()-9;
        }
        updateText(n);
    }
}
