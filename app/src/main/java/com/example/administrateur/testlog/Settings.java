package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void askforBackup(View view){
        //TODO : implement an event to ask for a backup (automail ?)

        String userMail;

        try{
            GMailSender sender = new GMailSender("achristophe.story.automail@gmail.com", "randomLongpassword"); //TODO : create this mail address
            sender.sendMail("Demande de backup", "Merci de faire un backup de la BDD !", userMail, "thomasprevost85@gmail.com");
        } catch (Exception e){
            e.printStackTrace();
        }
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
