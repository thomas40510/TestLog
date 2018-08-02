package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class aboutScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_screen);

        ((TextView) findViewById(R.id.versiontxt)).setText(BuildConfig.VERSION_NAME);
    }

    public void goBack(View view){
        finish();
    }
}
