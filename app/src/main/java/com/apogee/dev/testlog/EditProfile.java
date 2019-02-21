/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    EditText bDate, address, city, forfait, remain;
    EditText phone, mail;
    RadioGroup radioGroup;
    RadioButton radioFather, radioMother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bDate = (EditText) findViewById(R.id.bDate);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        forfait = (EditText) findViewById(R.id.forfait);
        remain = (EditText) findViewById(R.id.remaining);
        phone = (EditText) findViewById(R.id.phone);
        mail = (EditText) findViewById(R.id.mail);

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);


        // Sets un-changing infos

        ((TextView)findViewById(R.id.nom)).setText(Profile.nameStr);
        ((TextView)findViewById(R.id.flechage)).setText(Profile.flechStr);
        ((TextView)findViewById(R.id.licence)).setText(Profile.licStr);


        // Sets textboxes' text as it appears in DB

        bDate.setText(Profile.bDateStr);
        address.setText(Profile.addressStr);
        city.setText(Profile.cityStr);
        forfait.setText(Profile.forfaitStr);
        remain.setText(Profile.remainStr);
        phone.setText(Profile.numberStr.replace("none", ""));
        mail.setText(Profile.mailStr.replace("none",""));

        switch (Profile.telWhoStr){
            case "Père":
                radioGroup.check(R.id.radioFather);
                //Log.e("DBG", "father");
                break;
            case "Mère":
                radioGroup.check(R.id.radioMother);
                //Log.e("DBG", "mother");
                break;
        }
    }


    /**
     * Handle DB-sided update of infos
     */

    public void save (View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavaliers");
        DatabaseReference mref = reference.child(Profile.nameStr);
        mref.child("Birthdate").setValue(bDate.getText().toString());
        mref.child("Forfait").setValue(forfait.getText().toString());
        mref.child("adresse").setValue(address.getText().toString());
        mref.child("ville").setValue(city.getText().toString());
        mref.child("remainH").setValue(remain.getText().toString());

        if(mail.getText().toString().isEmpty()){
            mref.child("mail").setValue("none");
        }
        else {
            mref.child("mail").setValue(mail.getText().toString());
        }

        if(phone.getText().toString().isEmpty()){
            mref.child("tel").child("nbr").setValue("none");
            mref.child("tel").child("who").setValue("mainWho");
        }
        else {
            String who = "";
            mref.child("tel").child("nbr").setValue(phone.getText().toString());
            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.radioFather:
                    who = "Père";
                    break;
                case R.id.radioMother:
                    who = "Mère";
                    break;
            }
            mref.child("tel").child("who").setValue(who);
        }


        Toast.makeText(this, "Profil modifié !", Toast.LENGTH_SHORT).show();
        finish();
    }
}
