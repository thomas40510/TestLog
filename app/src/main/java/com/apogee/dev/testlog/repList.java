package com.apogee.dev.testlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class repList extends AppCompatActivity {

    public String day;
    public static List<String> repList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_list);

        repList.clear();

        Bundle extras = getIntent().getExtras();
        day = extras.getString("day");

        final LinearLayout layout = (LinearLayout) findViewById(R.id.linLayout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("reprises");
        DatabaseReference dayRef = myref.child(day);

        dayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    repList.add(postsnapshot.getKey());
                }
                for (final String s:repList){
                    Button repButton = new Button(repList.this);
                    String bText;
                    try{
                        bText = s+" ("+dataSnapshot.child(s).child("niveau").getValue().toString()+")";
                    } catch (Exception e){
                        e.printStackTrace();
                        bText = "error";
                    }
                    repButton.setText(bText);
                    repButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openRep(s,day);
                        }
                    });
                    repButton.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout.addView(repButton);
                    //Log.e("DBG", "reached !");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void openRep(String repName, String day){
        Intent intent = new Intent(this, repInfo.class);
        intent.putExtra("reprise", repName).putExtra("day", day);
        startActivity(intent);
    }
}
