package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    public TextView bDate;
    public TextView address;
    public TextView flechage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bDate = (TextView) findViewById(R.id.bDate);
        address = (TextView) findViewById(R.id.address);
        flechage = (TextView) findViewById(R.id.flechage);

        Bundle extras = getIntent().getExtras();
        String nameStr = extras.getString("name");

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(nameStr);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        DatabaseReference nameRef = myref.child(nameStr);

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bDate.setText(dataSnapshot.child("Birthdate").getValue(String.class));
                address.setText((dataSnapshot.child("adresse").getValue(String.class)+", "+dataSnapshot.child("ville").getValue(String.class)));
                flechage.setText(dataSnapshot.child("fléchage").getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
