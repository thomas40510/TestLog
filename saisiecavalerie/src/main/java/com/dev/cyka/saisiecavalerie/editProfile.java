package com.dev.cyka.saisiecavalerie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class editProfile extends AppCompatActivity {

    public EditText bDate, limhr, proprio, lastnico;
    public EditText rationMt, rationMd, rationS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        bDate = (EditText) findViewById(R.id.bDate);
        limhr = (EditText) findViewById(R.id.limhr);
        proprio =(EditText) findViewById(R.id.proprio);
        lastnico = (EditText) findViewById(R.id.lastnico);

        rationMt = (EditText) findViewById(R.id.rationMt);
        rationMd = (EditText) findViewById(R.id.rationMd);
        rationS = (EditText) findViewById(R.id.rationS);


        // Sets un-changing infos

        ((TextView)findViewById(R.id.name)).setText(PonyProfile.nameStr);
        ((TextView)findViewById(R.id.sexe)).setText(PonyProfile.sexeStr);

        // Sets textboxes' text as it appears in DB

        bDate.setText(PonyProfile.bDateStr);
        limhr.setText(PonyProfile.limhrStr);
        proprio.setText(PonyProfile.proprioStr);
        lastnico.setText(PonyProfile.nicoStr);

        rationMt.setText(PonyProfile.rationStr[0]);
        rationMd.setText(PonyProfile.rationStr[1]);
        rationS.setText(PonyProfile.rationStr[2]);
    }


    /**
     * Handle DB-sided update of infos
     */

    public void save (View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavalerie");
        DatabaseReference mref = reference.child(PonyProfile.nameStr);
        mref.child("bdate").setValue(bDate.getText().toString());
        mref.child("limhr").setValue(limhr.getText().toString());
        mref.child("proprio").setValue(proprio.getText().toString());
        mref.child("lastNico").setValue(lastnico.getText().toString());

        DatabaseReference mref1 = mref.child("ration");
        mref1.child("mt").setValue(rationMt.getText().toString());
        mref1.child("md").setValue(rationMd.getText().toString());
        mref1.child("s").setValue(rationS.getText().toString());

        Toast.makeText(this, "Profil modifié !", Toast.LENGTH_SHORT).show();
        finish();
    }

}
