package com.apogee.dev.testlog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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

    public void showInfos(){
        String infos = "Version : "+ BuildConfig.VERSION_NAME + "\n"
                + "Version code : "+BuildConfig.VERSION_CODE + "\n"
                + "Build date : "+ getString(R.string.BUILD_DATE)+ "\n"
                + "Package : "+BuildConfig.APPLICATION_ID;

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
                });
        builder.show();
    }

    public void goBack(View view){
        finish();
    }
}
