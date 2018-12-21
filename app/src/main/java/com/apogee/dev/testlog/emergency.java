/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class emergency extends AppCompatActivity {

    private boolean isContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        try {
            Bundle extras = getIntent().getExtras();
            isContact = extras.getBoolean("isContact");
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        ((TextView) findViewById(R.id.title)).setText((isContact) ? "Numéros d'urgence" : "Lieux d'intérêt - coordonnées GPS");
        ((TextView) findViewById(R.id.details)).setText(getResources().getString((isContact) ? R.string.emergency_numbers : R.string.emergency_gps));
    }
}
