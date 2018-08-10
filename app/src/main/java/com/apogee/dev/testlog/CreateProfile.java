package com.apogee.dev.testlog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfile extends AppCompatActivity {

    EditText name, flechage, bDate, address, city, remain,phonenbr,mail;
    RadioButton radiofather, radiomother;
    String whoStr, mailStr;
    AutoCompleteTextView forfait;

    //TODO : ajouter n° licence ds création profile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        name = (EditText) findViewById(R.id.name);
        flechage = (EditText) findViewById(R.id.flechage);
        bDate = (EditText) findViewById(R.id.bDate);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        forfait = (AutoCompleteTextView) findViewById(R.id.forfait);
        remain = (EditText) findViewById(R.id.remaining);
        phonenbr = (EditText) findViewById(R.id.phonenbr);
        mail = (EditText) findViewById(R.id.mail);

        radiofather = (RadioButton) findViewById(R.id.radioFather);
        radiomother = (RadioButton) findViewById(R.id.radioMother);

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

        mail.setOnKeyListener(listener);
        flechage.setNextFocusDownId(R.id.name);
        name.setNextFocusDownId(R.id.bDate);
        bDate.setNextFocusDownId(R.id.address);
        address.setNextFocusDownId(R.id.city);
        city.setNextFocusDownId(R.id.forfait);
        forfait.setNextFocusDownId(R.id.remaining);
        remain.setNextFocusDownId(R.id.phonenbr);
        phonenbr.setNextFocusDownId(R.id.mail);

        if(radiomother.isChecked() || radiofather.isChecked()){
            if(radiomother.isChecked()){
                whoStr = "mère";
            } else{
                whoStr = "père";
            }
        } else{
            whoStr = "mainWho";
        }

        mailStr = mail.getText().toString();
        if (mailStr.equals("")){
            mailStr = "none";
        }


        String[] cardRefs = getResources().getStringArray(R.array.cardRefs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cardRefs);
        forfait.setAdapter(adapter);
        forfait.setThreshold(1);


    }

    /**
     * Handle DB-sided update of infos
     */

    public void save (View view){

        if (verifDate(bDate.getText().toString())) {


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("cavaliers");
            DatabaseReference mref = reference.child(name.getText().toString());


            mref.child("fléchage").setValue(flechage.getText().toString());
            mref.child("Birthdate").setValue(bDate.getText().toString());
            mref.child("Forfait").setValue(forfait.getText().toString());
            mref.child("adresse").setValue(address.getText().toString());
            mref.child("ville").setValue(city.getText().toString());
            mref.child("remainH").setValue(remain.getText().toString());
            mref.child("tel").child("nbr").setValue(phonenbr.getText().toString());
            mref.child("tel").child("who").setValue(whoStr);
            mref.child("mail").setValue(mailStr);

            Answers.getInstance().logCustom(new CustomEvent("Added user")
                    .putCustomAttribute("Name", name.getText().toString()));

            Toast.makeText(this, "Utilisateur Créé", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Erreur")
                    .setMessage("La date entrée est incorrecte. Merci de vérifier les valeurs et le format (dd/mm/aaaa")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }

    public boolean verifDate(String date){
        int[][] list = new int[][] {{1,31},{2,28},{3,31},{4,30},{5,31},{6,30},{7,31},{8,31},{9,30},{10,31},{11,30},{12,31}};

        int month = Integer.parseInt(""+date.charAt(3)+date.charAt(4));
        if (month>0 && month<13){
            int day = Integer.parseInt(""+date.charAt(0)+date.charAt(1));
            int max = list[month-1][1];

            return (day>0 && day<max+1);
        }
        return false;
    }
}
