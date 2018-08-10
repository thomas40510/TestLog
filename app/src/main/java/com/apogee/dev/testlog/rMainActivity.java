package com.apogee.dev.testlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class rMainActivity extends AppCompatActivity {

    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("nextScreen", "reprises");
            startActivity(intent);
            finish();
        }

        prefs = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);


        ((Button) findViewById(R.id.buttonMercredi)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRep("mercredi");
            }
        });
        ((Button) findViewById(R.id.buttonSamedi)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRep("samedi");
            }
        });

        new DBFetch().fetchDB(false, false);
    }

    public void gotoRep(String day){
        Intent intent = new Intent(this, repList.class);
        intent.putExtra("day", day);
        startActivity(intent);
    }

    public void goBack(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void test(View view){
        List<String> l = new ArrayList<>();
        l.clear();
        for (int i = 1;i<10; i++){
            l.add(""+i);
        }
    }
}
