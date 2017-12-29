package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    EditText bDate, address, city, forfait, remain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bDate = (EditText) findViewById(R.id.bDate);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        forfait = (EditText) findViewById(R.id.forfait);
        remain = (EditText) findViewById(R.id.remaining);


        // Sets un-changing infos

        ((TextView)findViewById(R.id.name)).setText(Profile.nameStr);
        ((TextView)findViewById(R.id.flechage)).setText(Profile.flechStr);


        // Sets textboxes' text as it appears in DB

        bDate.setText(Profile.bDateStr);
        address.setText(Profile.addressStr);
        city.setText(Profile.cityStr);
        forfait.setText(Profile.forfaitStr);
        remain.setText(Profile.remainStr);
    }


    /**
     * Handle DB-sided update of infos
     */

    public void save (View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        DatabaseReference mref = reference.child(Profile.nameStr);
        mref.child("Birthdate").setValue(bDate.getText().toString());
        mref.child("Forfait").setValue(forfait.getText().toString());
        mref.child("adresse").setValue(address.getText().toString());
        mref.child("ville").setValue(city.getText().toString());
        mref.child("remainH").setValue(remain.getText().toString());
    }
}
