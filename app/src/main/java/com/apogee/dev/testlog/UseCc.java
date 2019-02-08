/*
 * Copyright (c) 2019 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UseCc extends AppCompatActivity {

    String cardnumber;
    private Boolean isValid;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_cc);

    }
    public void verifnumber(View view){
        findViewById(R.id.infosLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnUse).setVisibility(View.INVISIBLE);

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("cartes cadeau");

        cardnumber = ((EditText)findViewById(R.id.saisieCarte)).getText().toString();
            ref.child(cardnumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        dataSnapshot.getValue().toString();
                        getsetInfos(cardnumber, true);
                        findViewById(R.id.btnUse).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getsetInfos(cardnumber, false);
                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                        AlertDialog.Builder builder = new AlertDialog.Builder(UseCc.this);
                        builder.setTitle("Erreur")
                                .setMessage("Le numéro rentré ne correspond pas à une carte cadeau valide. Merci de réessayer.")
                                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ((EditText)findViewById(R.id.saisieCarte)).setText("");
                                    }
                                });
                        builder.show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    public void getsetInfos(final String cardnumber,Boolean get){

        final TextView infosTxt = (TextView) findViewById(R.id.cardinfos);
        final TextView titleTxt = (TextView) findViewById(R.id.cardnumber);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference().child("cartes cadeau").child(cardnumber);
        if (get) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String infos = "<b>Prestation : </b>" + dataSnapshot.child("quoi").getValue(String.class) + "<br>" +
                            "<b>Valeur : </b>" + dataSnapshot.child("prix").getValue(String.class) + " € <br>" +
                            "<b>Réglée le : </b>" + dataSnapshot.child("quand").getValue(String.class) + "<b> en </b> " +
                            "" + dataSnapshot.child("paiement").getValue(String.class) + "<b> à </b>" + dataSnapshot.child("qui").getValue(String.class) + "<br><br>" +
                            "<b>Valable jusq'au : </b>" + dataSnapshot.child("val").getValue(String.class) + "<br><br>" +
                            (!dataSnapshot.child("used").getValue(String.class).equals("/") ? "<b>Utilisée le : </b>" + dataSnapshot.child("used").getValue(String.class) : "");
                    infosTxt.setText(Html.fromHtml(infos));

                    titleTxt.setText("Carte n°" + cardnumber);

                    if (checkValid(dataSnapshot.child("val").getValue(String.class), dataSnapshot.child("used").getValue(String.class))) {
                        titleTxt.setTextColor(getResources().getColor(R.color.adok));
                        findViewById(R.id.btnUse).setEnabled(true);
                        ((Button)findViewById(R.id.btnUse)).setText("Utilier la carte cadeau");
                    } else {
                        titleTxt.setTextColor(getResources().getColor(R.color.adexpired));
                        findViewById(R.id.btnUse).setEnabled(false);
                        ((Button)findViewById(R.id.btnUse)).setText("Carte non valable");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            findViewById(R.id.infosLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.btnUse).setVisibility(View.VISIBLE);
        } else{
            if (isValid){
                AlertDialog.Builder builder = new AlertDialog.Builder(UseCc.this);
                builder.setTitle("Confirmation")
                        .setMessage("Voulez-vous utiliser la carte cadeau n°"+cardnumber+" ? Si vous cconfirmez, elle ne sera plus valide " +
                                "et il n'y a pas de retour en arrière possible.")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ref.child("state").setValue("utilisée");
                                ref.child("used").setValue(sdf.format(new Date()));
                                Toast.makeText(getApplicationContext(), "Carte cadeau utilisée.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.show();
            }
        }

    }

    private Boolean checkValid(String date, String used){

        Date strDate;
        try {
            strDate = sdf.parse(date);
            isValid = !(new Date().after(strDate)) && used.equals("/") ;
        } catch (Exception e){
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }
}
