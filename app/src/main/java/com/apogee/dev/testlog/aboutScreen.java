package com.apogee.dev.testlog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class aboutScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_screen);

        TextView versionTxt = (TextView) findViewById(R.id.versiontxt);
        versionTxt.setText(BuildConfig.VERSION_NAME);

        versionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfos();
            }
        });
    }

    Boolean BSmode;

    public void showInfos(){
        //TODO : implement 'bluestacks mode' for pdf generation

        SharedPreferences pref = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);
        BSmode = pref.getBoolean("BSmode", false);

        String infos = "Version : "+ BuildConfig.VERSION_NAME + "\n"
                + "Version code : "+BuildConfig.VERSION_CODE + "\n"
                + "Build date : "+ getString(R.string.BUILD_DATE)+ "\n"
                + "Package : "+BuildConfig.APPLICATION_ID + "\n"
                +"Bluestacks : "+BSmode;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Plus d'infos...")
                .setMessage(infos)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("Changelog", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse("https://github.com/thomas40510/TestLog/blob/master/docs/changelog.md");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("set BS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (MainMenu.loggedUserName.equals("Admin")) {
                            setBluestacks();

                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Droits insuffisants",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.show();
    }

    public void goBack(View view){
        finish();
    }

    public void setBluestacks(){
        AlertDialog.Builder bsbuilder = new AlertDialog.Builder(this);
        bsbuilder.setTitle("set Bluestacks status")
                .setMessage("BSmode = "+BSmode)
                .setPositiveButton("Activer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeBS(true);
                    }
                })
                .setNegativeButton("Désactiver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeBS(false);
                    }
                })
                .setNeutralButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        bsbuilder.show();
    }
    public void changeBS(Boolean bsmode){
        SharedPreferences.Editor e = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE).edit();
        e.putBoolean("BSmode", bsmode);
        e.commit();

        Log.e("DBG", ""+bsmode);
        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
    }
}
