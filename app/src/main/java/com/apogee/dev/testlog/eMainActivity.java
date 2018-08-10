package com.apogee.dev.testlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class eMainActivity extends AppCompatActivity {

    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("nextScreen", "reprises");
            startActivity(intent);
            finish();
        }

        prefs = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);

        ((Button)findViewById(R.id.bFront)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNext("devant");
            }
        });

        ((Button)findViewById(R.id.bBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNext("derrière");
            }
        });
        ((Button)findViewById(R.id.bPoney)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNext("poney");
            }
        });
        ((Button)findViewById(R.id.bPrivate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNext("private");
            }
        });

        new DBFetch().cfetchDB(false);
    }

    public void gotoNext (String where){
        Intent intent = new Intent(this, stabInfo.class);
        intent.putExtra("where", where);
        startActivity(intent);
    }

    public void goBack(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
