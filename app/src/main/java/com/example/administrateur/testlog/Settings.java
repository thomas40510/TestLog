package com.example.administrateur.testlog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

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
        //TODO : implement an event to ask for a backup (automail ?)

        /*
        try{
            GMailSender sender = new GMailSender("achristophe.story.automail@gmail.com", "randomLongpassword"); //TODO : create this mail address
            sender.sendMail("Demande de backup", "Merci de faire un backup de la BDD !", userMail, "thomasprevost85@gmail.com");
        } catch (Exception e){
            e.printStackTrace();
        }
        */
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Confirmez-vous la demande d'un backup de la base de données ? (en sachant qu'il y en a un automatique chaque jour)")
                .setPositiveButton("Je confirme", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            sendMail sendMail = new sendMail(Settings.this,"thomasprevost85@gmail.com", "Demande de backup", "Merci de faire un backup de la BDD ! \n\n ***************************************** \n " +
                                    "Message sender : "+userMail+ "\n FCM : "+fcm);
                            sendMail.execute();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Erreur d'envoi...", Toast.LENGTH_SHORT).show();
                        }
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
        //TODO : same for restore
    }
    public void delLocal(View view){
        //TODO : implement a method to delete all local files (including sharedPreferences)
    }
    public void openMsg(View view){
        //TODO : implement Smooch messaging
    }
}
