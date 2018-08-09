/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.example.administrateur.testlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;



public class Settings extends AppCompatActivity {

    private String userMail;
    private String fcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);
        userMail = preferences.getString("lastMail", null);

        fcm = FirebaseInstanceId.getInstance().getToken();
    }

    public void askforBackup(View view){

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Confirmez-vous la demande d'un backup de la base de données ? (en sachant qu'il y en a un automatique chaque jour)")
                .setPositiveButton("Je confirme", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sendAutoMail("Demande de backup", "Merci de faire un backup de la BDD !");

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();

    }
    public void askforRestore(View view){

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment demander la restoration de la base de données à partir d'une sauvegarde précédente ? (Il ne faut pas hésiter à envoyer un message au dev s'il faut récupérer une sauvegarde précise)")
                .setPositiveButton("Je confirme", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sendAutoMail("Demande de restore", "Merci de restorer la BDD à partir d'une sauvegarde plus ancienne !");

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
    public void delLocal(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Êtes-vous sûr de vouloir effacer toutes les données locales ? (c'est-à-dire l'intégralité des fichiers créés, ainsi que le cache de l'application). Vous serez alors déconnecté de l'application.")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences prefs = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.commit();

                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir/generated";

                        File dir = new File(path);

                        if (dir.isDirectory()){
                            String[] children = dir.list();
                            for (int s = 0; i<children.length; i++){
                                new File(dir, children[s]).delete();
                            }
                        }

                        Toast.makeText(getApplicationContext(), "Les données locales ont été supprimées", Toast.LENGTH_SHORT).show();

                        gotoAuth();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }

    public void gotoAuth (){
        Intent intent = new Intent(this, LoginActivity.class);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        auth.signOut();
        startActivity(intent);
        finish();
    }

    public void chooseMean(View view){
        AlertDialog.Builder chooser = new AlertDialog.Builder(this);
        chooser.setTitle("Sélection")
                .setMessage("Comment voulez-vous contacter le développeur ?")
                .setPositiveButton("Par mail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendMsg("mail");
                    }
                })
                .setNegativeButton("Par SMS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendMsg("SMS");
                    }
                })
                .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        chooser.show();
    }

    public void sendMsg(final String mean){

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Message à envoyer :");

        final EditText editText = new EditText(this);
        editText.setSingleLine(false);
        editText.setLines(4);
        editText.setMaxLines(5);
        editText.setGravity(Gravity.LEFT | Gravity.TOP);
        editText.setHorizontalScrollBarEnabled(false);
        editText.setHint("Message...");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);

        builder.setView(layout);

        builder.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mean.equals("mail")) {
                    sendAutoMail("Message from user", "---Begin user message--- \n" + editText.getText().toString() + "\n ---End user message---");
                } else {
                    sendSMS(editText.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void sendSMS(String message){


        String number = "+33621041300";

        try {

            SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
            Toast.makeText(this, "Message envoyé", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de l'envoi du message", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendAutoMail(String subject, String message){
        try{
            sendMail sendMail = new sendMail(Settings.this,"thomasprevost85@gmail.com", subject, message+"\n\n ***************************************** \n " +
                    "Message sender : "+userMail+ "\n FCM : "+fcm);
            sendMail.execute();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Erreur d'envoi...", Toast.LENGTH_SHORT).show();
        }
    }


    public void goBack(View view){
        finish();
    }
}
