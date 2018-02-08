package com.dev.cyka.saisiecavalerie;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewPony extends AppCompatActivity {

    public EditText bDate, limhr, proprio, lastnico, name;
    public EditText rationMt, rationMd, rationS;
    public CheckBox isClub;
    public Spinner sexe, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pony);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        isClub = (CheckBox) findViewById(R.id.checkClub);
        sexe = (Spinner) findViewById(R.id.Sspinner);
        sexe.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.sexes)));
        type = (Spinner) findViewById(R.id.Tspinner);
        type.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.typelist)));

        bDate = (EditText) findViewById(R.id.bDate);
        limhr = (EditText) findViewById(R.id.limhr);
        proprio = (EditText) findViewById(R.id.proprio);
        lastnico = (EditText) findViewById(R.id.lastnico);
        name = (EditText) findViewById(R.id.name);

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

    }


    /**
     * Handle DB-sided update of infos
     */

    public void save (View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavalerie");
        if(name.getText().toString().equals("")){
            Toast.makeText(this, "No name entered", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            DatabaseReference mref = reference.child(name.getText().toString());
            mref.child("sex").setValue(sexe.getSelectedItem().toString());
            mref.child("type").setValue(type.getSelectedItem().toString());
            mref.child("bdate").setValue(bDate.getText().toString());
            mref.child("limhr").setValue(limhr.getText().toString());

            if (isClub.isChecked()) {
                mref.child("proprio").setValue("club");
            } else {
                mref.child("proprio").setValue(proprio.getText().toString());
            }
            mref.child("lastNico").setValue(lastnico.getText().toString());

            DatabaseReference mref1 = mref.child("ration");
            mref1.child("mt").setValue(rationMt.getText().toString());
            mref1.child("md").setValue(rationMd.getText().toString());
            mref1.child("s").setValue(rationS.getText().toString());

            Toast.makeText(this, "équidé ajouté !", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
