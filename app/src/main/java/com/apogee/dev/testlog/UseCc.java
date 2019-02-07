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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UseCc extends AppCompatActivity {

    String cardnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_cc);

    }
    public void verifnumber(View view){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("cartes cadeau");

        cardnumber = ((EditText)findViewById(R.id.saisieCarte)).getText().toString();
            Log.e("DBG", cardnumber);
            ref.child(cardnumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        dataSnapshot.getValue().toString();
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
                    } finally {
                        getInfos(cardnumber);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    public void getInfos(final String cardnumber){
        final TextView infosTxt = (TextView) findViewById(R.id.cardinfos);
        final TextView titleTxt = (TextView) findViewById(R.id.cardnumber);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("cartes cadeau").child(cardnumber);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String infos = "<b>Prestation : </b>"+dataSnapshot.child("quoi").getValue(String.class)+"<br>" +
                        "<b>Valeur : </b>"+dataSnapshot.child("prix").getValue(String.class)+"<br>" +
                        "<b>Réglée le : </b>"+dataSnapshot.child("quand").getValue().toString()+"<b> en </b> " +
                        ""+dataSnapshot.child("paiement").getValue(String.class)+"<b> à </b>"+dataSnapshot.child("qui").getValue(String.class)+"<br><br>" +
                        "<b>Valable jusq'au : </b>"+dataSnapshot.child("val").getValue(String.class);
                infosTxt.setText(Html.fromHtml(infos));

                titleTxt.setText("Carte n°"+cardnumber);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date strDate = sdf.parse(dataSnapshot.child("val").getValue(String.class));
                    if (new Date().after(strDate)) {
                        titleTxt.setTextColor(getResources().getColor(R.color.adexpired));
                    } else{
                        titleTxt.setTextColor(getResources().getColor(R.color.adok));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        findViewById(R.id.infosLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.btnUse).setVisibility(View.VISIBLE);
    }
}
