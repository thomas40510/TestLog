package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class showVV extends AppCompatActivity {

    private String nameStr, type, dbChildName;
    private List<String> dateList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private String completeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vv);

        Bundle extras = getIntent().getExtras();
        nameStr = extras.getString("name");
        type = extras.getString("type");

        Log.e("DBG @ln31", type + "-" + nameStr);

        TextView titleTxt = (TextView) findViewById(R.id.titleTxt);

        if (type.equals("verm")) {
            dbChildName = "lastverm";
            titleTxt.setText(nameStr+" - "+"Derniers vermifuges");
        } else {
            dbChildName = "lastvacs";
            titleTxt.setText(nameStr+" - "+"Derniers vaccins");

        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavalerie").child(nameStr).child(dbChildName);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dateList.clear();
                nameList.clear();

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String date = postsnapshot.getKey();
                    dateList.add(date);

                    String name = postsnapshot.getValue().toString();
                    Log.e("DBG", name);
                    nameList.add(name);
                }
                Log.d("INFO", dateList.toString() + dateList.size());
                showList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showList() {
        completeList = "";
        TextView listView = (TextView) findViewById(R.id.vermvacTextView);
        for (int i = dateList.size()-1; i>-1; i--) {
            completeList = completeList.concat(dateList.get(i) + " : " + nameList.get(i) + "\n\n");
        }
        Log.e("DBG", completeList);
        listView.setText(completeList);
    }
}
