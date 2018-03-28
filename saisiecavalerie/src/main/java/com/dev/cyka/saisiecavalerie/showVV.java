package com.dev.cyka.saisiecavalerie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class showVV extends AppCompatActivity {

    private String nameStr, type, dbChildName;
    private List<String> dateList;
    private List<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vv);

        Bundle extras = getIntent().getExtras();
        nameStr = extras.getString("name");
        type = extras.getString("type");

        if (type.equals("verm")){
            dbChildName = "lastverm";
        }
        else{
            dbChildName = "lastvacs";
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavalerie").child(dbChildName);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dateList.clear();

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    Iterable postpostsnapshot = dataSnapshot.getChildren();
                    String date = postsnapshot.getKey()+"-"+postsnapshot.getChildren()+"-"+"(get children of children";
                    dateList.add(date);
                }
                Log.d("INFO", dateList.toString()+dateList.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
