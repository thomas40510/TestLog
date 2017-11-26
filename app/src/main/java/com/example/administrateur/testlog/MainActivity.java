package com.example.administrateur.testlog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
    public View recentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        n = 1;
        updateValue(recentView);

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
                //updateText(1);
                display();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /**public void updateText (int n){
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
    }*/
    public void display (){
        //View recentView = findViewById(R.id.button2);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(linearLayout);
        setContentView(scroll);
        for(int i = 0; i<userlist.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(userlist.get(i));
            linearLayout.addView(textView);
        }
    }
    public void disp(){
        RelativeLayout relativeLayout = new RelativeLayout(this);
        TextView textView = new TextView(this);
        textView.setId((int)System.currentTimeMillis());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, findViewById(R.id.button2).getId());
        textView.setText("hey");
        relativeLayout.addView(textView);
    }
}
