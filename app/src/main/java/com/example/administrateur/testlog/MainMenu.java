package com.example.administrateur.testlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void gotoMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void gotoCaval(View view){
        Intent intent = new Intent(this, cMainActivity.class);
        startActivity(intent);
    }
}
