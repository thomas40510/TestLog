/*
 * Copyright (c) 2019 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.smooch.ui.ConversationActivity;

public class CreateCc extends AppCompatActivity {


    EditText presta, value, datepicker;
    String prestaStr, valStr, dateStr, payStr, maxdate;
    Spinner paymethod;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cc);

        presta =  findViewById(R.id.presta);
        value = findViewById(R.id.valeur);
        paymethod = findViewById(R.id.paymethod);
        datepicker = findViewById(R.id.datepicker);
    }

    public void setToday(View view){
        datepicker.setText(sdf.format(new Date()));
    }

    public void confirm2(View view){
        try {
            dateStr = datepicker.getText().toString();
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(dateStr));
            cal.add(Calendar.MONTH, 6);

            Log.e("DBG", sdf.format(cal.getTime()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void confirm(View view){
        AlertDialog.Builder build = new AlertDialog.Builder(CreateCc.this);
        build.setTitle("Confirmation")
                .setMessage("confirmez-vous la saisie des informations ? Une fois la carte enregistrée, il n'est pas possible de revenir en arrière.")
                .setPositiveButton("Je confirme !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            prestaStr = presta.getText().toString();
                            valStr = value.getText().toString();
                            dateStr = datepicker.getText().toString();
                            payStr = paymethod.getSelectedItem().toString();

                            cal.setTime(sdf.parse(dateStr));
                            cal.add(Calendar.MONTH, 6);

                            new asyncTask().execute();
                        } catch (Exception e){
                            e.printStackTrace();
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateCc.this);
                            builder.setTitle("Erreur")
                                    .setMessage("Une erreur s'est produite. La carte n'a pas été validée. \n" +
                                            "Assurez-vous d'avoir rempli toutes les informations correctement.\n" +
                                            "Si le problème persiste, notez les infos sur papier et contactez le développeur.")
                                    .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setNegativeButton("Contact", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ConversationActivity.show(CreateCc.this);
                                        }
                                    });
                            builder.show();
                        }
                    }
                })
                .setNegativeButton("Je re-vérifie...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        build.show();

    }

    Calendar cal = Calendar.getInstance();
    int cardnumber;

    private class asyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute(){


        }
        @Override
        protected Void doInBackground(Void... params) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("cartes cadeau");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cardnumber = Integer.parseInt(dataSnapshot.child("next").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            SystemClock.sleep(1000);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            if(cardnumber<2019){
                Log.e("DBG", "reroll "+cardnumber);
                new asyncTask().execute();
            } else {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference newref = database.getReference().child("cartes cadeau").child(Integer.toString(cardnumber));
                newref.child("paiement").setValue(payStr);
                newref.child("prix").setValue(valStr);
                newref.child("quand").setValue(dateStr);
                newref.child("qui").setValue(MainMenu.loggedUserName);
                newref.child("quoi").setValue(prestaStr);
                newref.child("state").setValue("valide");
                newref.child("used").setValue("/");
                newref.child("val").setValue(sdf.format(cal.getTime()));


                String message = "=============================================\n" +
                        "==== Carte cadeau - confirmation de l'enregistrement ====\n" +
                        "=============================================\n\n" +
                        "Carte numéro : " + cardnumber + "\n" +
                        "Valable pour : " + prestaStr + "\n" +
                        "D'une valeur de : " + valStr + " € (payé en " + payStr + ") \n" +
                        "Enregistrée par : " + MainMenu.loggedUserName + " le " + sdf.format(new Date()) + "\n" +
                        "Valable du " + dateStr + " au " + sdf.format(cal.getTime()) + "\n\n" +
                        "********************************\n" +
                        "user : " + MainMenu.userMail + "; fcm : " + FirebaseInstanceId.getInstance().getToken();

                try {
                    String[] recipients = {MainMenu.userMail, "thomasprevost85@gmail.com"};
                    sendMail sendMail = new sendMail(CreateCc.this, recipients, "Carte cadeau enregistrée n°" + cardnumber, message);
                    sendMail.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erreur d'envoi...", Toast.LENGTH_SHORT).show();
                }

                database.getReference().child("cartes cadeau").child("next").setValue(Integer.toString(cardnumber + 1));
                confirmSaisie();
            }
        }
    }

    public void confirmSaisie(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateCc.this);
        builder.setTitle("Carte enregistrée !");

        String msg = "Carte n° : "+cardnumber+"\n" +
                "Prestation : "+prestaStr+" (valeur : "+valStr+", payé en "+payStr+") \n" +
                "Valable du "+dateStr+" au "+sdf.format(cal.getTime())+"\n\n" +
                "Un mail de confirmation, contenant toutes ces infos, a été envoyé à l'adresse "+MainMenu.userMail+".";
        builder.setMessage(msg)
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();


    }

}
