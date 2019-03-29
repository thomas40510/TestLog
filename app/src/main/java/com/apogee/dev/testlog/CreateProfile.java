/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfile extends AppCompatActivity {

    EditText lname, name, flechage, bDate, address, city, remain,phonenbr,mail, licnbr;
    RadioButton radiofather, radiomother;
    String whoStr, mailStr;
    AutoCompleteTextView forfait;
    String Nom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        lname = (EditText) findViewById(R.id.nom);
        name = (EditText) findViewById(R.id.prénom);
        flechage = (EditText) findViewById(R.id.flechage);
        bDate = (EditText) findViewById(R.id.bDate);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        forfait = (AutoCompleteTextView) findViewById(R.id.forfait);
        remain = (EditText) findViewById(R.id.remaining);
        phonenbr = (EditText) findViewById(R.id.phonenbr);
        mail = (EditText) findViewById(R.id.mail);
        licnbr = (EditText) findViewById(R.id.licnbr);

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

        licnbr.setOnKeyListener(listener);
        flechage.setNextFocusDownId(R.id.nom);
        lname.setNextFocusDownId(R.id.prénom);
        name.setNextFocusDownId(R.id.bDate);
        bDate.setNextFocusDownId(R.id.address);
        address.setNextFocusDownId(R.id.city);
        city.setNextFocusDownId(R.id.forfait);
        forfait.setNextFocusDownId(R.id.remaining);
        remain.setNextFocusDownId(R.id.phonenbr);
        phonenbr.setNextFocusDownId(R.id.mail);
        mail.setNextFocusDownId(R.id.licnbr);

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

        findViewById(R.id.calimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });


    }

    public void selectDate(){
        final AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Saisie d'une date");
        builder.setMessage("Entrez la date de début du forfait. Peu importe le jour, seuls le mois et l'année sont gardés.");


        final DatePicker picker = new DatePicker(this);
        picker.setCalendarViewShown(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(picker);

        builder.setView(layout);

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int m = picker.getMonth()+1;
                String month = ""+picker.getYear()+"-"+(Integer.toString(m).length() == 1 ? 0+Integer.toString(m) : Integer.toString(m));
                remain.setText(month);
                remain.setClickable(false);
                remain.setFocusable(false);

                forfait.setText("FT");
                ////Log.e("date", date);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    /**
     * Handle DB-sided update of infos
     */

    public void save (View view) {

        try{
            String lnom = lname.getText().toString().toUpperCase();
            String nom = name.getText().toString();
            if (nom.equals("") || lnom.equals("")){
                throw (new NullPointerException());
            }
            Nom = lnom.replace(" ","") + " "+nom.replace(" ", "");
        } catch (Exception e){
            Toast.makeText(this, "Erreur. Merci de vérifier que le nom a été rentré correctement.", Toast.LENGTH_SHORT).show();
        }
        if (verifDate(bDate.getText().toString())) {

            try {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("cavaliers");
                DatabaseReference mref = reference.child(Nom);


                mref.child("fléchage").setValue(flechage.getText().toString());
                mref.child("Birthdate").setValue(bDate.getText().toString());
                mref.child("Forfait").setValue(forfait.getText().toString().toUpperCase());
                mref.child("adresse").setValue(address.getText().toString());
                mref.child("ville").setValue(city.getText().toString());
                mref.child("remainH").setValue(remain.getText().toString());
                mref.child("tel").child("nbr").setValue(phonenbr.getText().toString());
                mref.child("tel").child("who").setValue(whoStr);
                mref.child("mail").setValue(mailStr);
                mref.child("licNbr").setValue(licnbr.getText().toString());
                mref.child("adlic").setValue("1804");
                mref.child("isAssigned").setValue(false);

                Answers.getInstance().logCustom(new CustomEvent("Added user")
                        .putCustomAttribute("Name", name.getText().toString()));

                Toast.makeText(this, "Utilisateur créé. Pensez à renouveler son adhésion si besoin.", Toast.LENGTH_LONG).show();
                finish();
            } catch (Exception e){
                e.printStackTrace();
                AlertDialog.Builder b = new AlertDialog.Builder(CreateProfile.this);
                b.setTitle("Erreur")
                        .setMessage("Il y a eu une erreur. Vérifiez que les champs sont remplis, et que votre appareil est connecté à internet puis réessayez.")
                        .setPositiveButton("Compris", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("cavaliers");
                                reference.child(name.getText().toString()).setValue(null);
                            }
                        });
            }
            } else{
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
