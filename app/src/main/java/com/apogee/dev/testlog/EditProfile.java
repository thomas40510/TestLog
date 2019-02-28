/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    RadioButton radioFather, radioMother, radioOther;
    String othername = "";

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
        radioOther = (RadioButton) findViewById(R.id.radioOther);

        radioOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(EditProfile.this);
                b.setTitle("Vous avez choisi \"autre\"")
                        .setMessage("Merci de spécifier à qui appartient ce numéro de téléphone.");
                final EditText editText = new EditText(EditProfile.this);
                editText.setSingleLine(true);
                editText.setGravity(Gravity.LEFT | Gravity.TOP);
                editText.setHorizontalScrollBarEnabled(false);

                LinearLayout layout = new LinearLayout(EditProfile.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(editText);

                b.setView(layout);
                b.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            othername = editText.getText().toString();
                            radioOther.setText(othername);
                        } catch (Exception e){
                            Toast.makeText(EditProfile.this, "Erreur. Merci de renseigner le champ.", Toast.LENGTH_SHORT).show();
                        }
                        if (othername.equals("")){
                            Toast.makeText(EditProfile.this, "Merci de renseigner le champ.", Toast.LENGTH_SHORT).show();
                            radioGroup.check(R.id.radioFather);
                            radioOther.setText("Autre");
                        }
                    }
                });

                b.show();
            }
        });


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
            case "mainWho":
                break;
            default:
                radioGroup.check(R.id.radioOther);
                radioOther.setText(Profile.telWhoStr);
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
                case R.id.radioOther:
                    who = othername;
                    break;

            }
            mref.child("tel").child("who").setValue(who);
        }

        Toast.makeText(this, "Profil modifié !", Toast.LENGTH_SHORT).show();
        finish();
    }

}
