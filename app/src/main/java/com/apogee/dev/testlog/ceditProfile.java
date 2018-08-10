package com.apogee.dev.testlog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ceditProfile extends AppCompatActivity {

    public EditText bDate, limhr, proprio, lastnico;
    public EditText rationMt, rationMd, rationS;
    public CheckBox isClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cedit_profile);

        isClub = (CheckBox) findViewById(R.id.checkClub);

        bDate = (EditText) findViewById(R.id.bDate);
        proprio =(EditText) findViewById(R.id.proprio);
        lastnico = (EditText) findViewById(R.id.lastnico);

        rationMt = (EditText) findViewById(R.id.rationMt);
        rationMd = (EditText) findViewById(R.id.rationMd);
        rationS = (EditText) findViewById(R.id.rationS);

        View.OnKeyListener listener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        };
        lastnico.setNextFocusDownId(R.id.rationMt);
        rationMt.setNextFocusDownId(R.id.rationMd);
        rationMd.setNextFocusDownId(R.id.rationS);
        rationS.setOnKeyListener(listener);


        // Sets un-changing infos

        ((TextView)findViewById(R.id.name)).setText(PonyProfile.nameStr);
        ((TextView)findViewById(R.id.sexe)).setText(PonyProfile.sexeStr);
        ((TextView)findViewById(R.id.type)).setText(PonyProfile.typeStr);

        // Sets textboxes' text as it appears in DB

        bDate.setText(PonyProfile.bDateStr);
        proprio.setText(null);
        if (PonyProfile.proprioStr.equals("club")){
            isClub.setChecked(true);
        }
        else {
            proprio.setText(PonyProfile.proprioStr);
        }

        lastnico.setText(PonyProfile.nicoStr);

        rationMt.setText(PonyProfile.rationStr[0]);
        rationMd.setText(PonyProfile.rationStr[1]);
        rationS.setText(PonyProfile.rationStr[2]);
    }


    /**
     * Handle DB-sided update of infos
     */

    public void save (View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavalerie");
        DatabaseReference mref = reference.child(PonyProfile.nameStr);
        mref.child("bdate").setValue(bDate.getText().toString());

        if (isClub.isChecked()){
            mref.child("proprio").setValue("club");
        }
        else {
            mref.child("proprio").setValue(proprio.getText().toString());
        }
        mref.child("lastNico").setValue(lastnico.getText().toString());

        DatabaseReference mref1 = mref.child("ration");
        mref1.child("mt").setValue(rationMt.getText().toString());
        mref1.child("md").setValue(rationMd.getText().toString());
        mref1.child("s").setValue(rationS.getText().toString());

        Toast.makeText(this, "Profil modifié !", Toast.LENGTH_SHORT).show();
        finish();
    }

}
