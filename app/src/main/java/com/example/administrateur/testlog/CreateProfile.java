package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfile extends AppCompatActivity {

    EditText name, flechage, bDate, address, city, forfait, remain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        name = (EditText) findViewById(R.id.name);
        flechage = (EditText) findViewById(R.id.flechage);
        bDate = (EditText) findViewById(R.id.bDate);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        forfait = (EditText) findViewById(R.id.forfait);
        remain = (EditText) findViewById(R.id.remaining);

    }




    /**
     * Handle DB-sided update of infos
     */

    public void save (View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        DatabaseReference mref = reference.child(name.getText().toString());

        mref.child("fléchage").setValue(flechage.getText().toString());
        mref.child("Birthdate").setValue(bDate.getText().toString());
        mref.child("Forfait").setValue(forfait.getText().toString());
        mref.child("adresse").setValue(address.getText().toString());
        mref.child("ville").setValue(city.getText().toString());
        mref.child("remainH").setValue(remain.getText().toString());

        Toast.makeText(this, "Utilisateur Créé", Toast.LENGTH_SHORT).show();
        finish();
    }
}
