package com.example.administrateur.testlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    public TextView forfait;
    public  TextView remaining;
    public static String nameStr,bDateStr,addressStr,cityStr,flechStr,forfaitStr,remainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bDate = (TextView) findViewById(R.id.bDate);
        address = (TextView) findViewById(R.id.address);
        flechage = (TextView) findViewById(R.id.flechage);
        remaining = (TextView) findViewById(R.id.remaining);
        forfait = (TextView) findViewById(R.id.forfait);

        Bundle extras = getIntent().getExtras();
        nameStr = extras.getString("name");

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(nameStr);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        DatabaseReference nameRef = myref.child(nameStr);

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bDateStr = dataSnapshot.child("Birthdate").getValue(String.class);
                addressStr = dataSnapshot.child("adresse").getValue(String.class);
                cityStr = dataSnapshot.child("ville").getValue(String.class);
                flechStr = dataSnapshot.child("fléchage").getValue(String.class);
                forfaitStr = dataSnapshot.child("Forfait").getValue(String.class);
                remainStr = dataSnapshot.child("remainH").getValue(String.class);


                bDate.setText(bDateStr);
                address.setText(addressStr+", "+cityStr);
                flechage.setText(flechStr);
                forfait.setText(forfaitStr);
                remaining.setText(remainStr);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void editProfile (View view){
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra("bdate", bDateStr)
                .putExtra("address", addressStr)
                .putExtra("city", cityStr)
                .putExtra("forfait", forfaitStr)
                .putExtra("flechage", flechStr)
                .putExtra("remaining", remainStr);
        startActivity(intent);
    }
}
