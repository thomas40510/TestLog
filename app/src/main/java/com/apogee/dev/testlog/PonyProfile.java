/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PonyProfile extends AppCompatActivity {

    public View view, view2;
    public TextView bDate;
    public TextView proprio;
    public TextView sexe, type;
    public TextView lastNico;
    public TextView rationMt;
    public TextView rationMd;
    public TextView rationN;
    public TextView limhr;

    public static String nameStr,bDateStr,proprioStr,sexeStr, nicoStr, limhrStr, typeStr;
    public static String[] rationStr = {"N/A", "N/A", "N/A"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pony_profile);

        //TODO : ajouter ostéo + dentiste
        bDate = (TextView) findViewById(R.id.bDate);
        proprio = (TextView) findViewById(R.id.proprio);
        sexe = (TextView) findViewById(R.id.sexe);
        lastNico = (TextView) findViewById(R.id.lastnico);
        rationMt = (TextView) findViewById(R.id.rationMt);
        rationMd = (TextView) findViewById(R.id.rationMd);
        rationN = (TextView) findViewById(R.id.rationS);
        type = (TextView) findViewById(R.id.type);

        Bundle extras = getIntent().getExtras();
        nameStr = extras.getString("name");

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(nameStr);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavalerie");
        DatabaseReference nameRef = myref.child(nameStr);

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bDateStr = dataSnapshot.child("bdate").getValue(String.class);
                proprioStr = dataSnapshot.child("proprio").getValue(String.class);
                sexeStr = dataSnapshot.child("sex").getValue(String.class);
                typeStr = dataSnapshot.child("type").getValue(String.class);
                nicoStr = dataSnapshot.child("lastNico").getValue(String.class);

                rationStr[0] = dataSnapshot.child("ration").child("mt").getValue(String.class);
                rationStr[1] = dataSnapshot.child("ration").child("md").getValue(String.class);
                rationStr[2] = dataSnapshot.child("ration").child("s").getValue(String.class);


                bDate.setText(bDateStr);
                proprio.setText(proprioStr);
                sexe.setText("["+sexeStr+"]");
                type.setText(typeStr);
                lastNico.setText(nicoStr);

                rationMt.setText(rationStr[0]);
                rationMd.setText(rationStr[1]);
                rationN.setText(rationStr[2]);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * Actions for toolbar menu
     */
    @Override
    //load menu file//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ponyprofile_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.showverm:
                Intent intent = new Intent(this, showVV.class);
                intent.putExtra("type", "verm");
                intent.putExtra("name", nameStr);
                startActivity(intent);
                return true;

            case R.id.showvac:
                Intent intent1 = new Intent(this, showVV.class);
                intent1.putExtra("type", "vac");
                intent1.putExtra("name", nameStr);
                startActivity(intent1);
                return true;

            case R.id.deleteUser:
                confirmDelete(view);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void editProfile (View view){
        Intent intent = new Intent(this, ceditProfile.class);
        intent.putExtra("bdate", bDateStr)
                .putExtra("proprio", proprioStr)
                .putExtra("sex", sexeStr)
                .putExtra("lastnico", nicoStr)
                .putExtra("ration", rationStr);
        startActivity(intent);
    }


    public void confirmDelete (View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation...")
                .setMessage("Êtes-vous sûr de vouloir supprimer cet équidé ? Il n'y a pas de retour en arrière possible...")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        usrDelete(view2);
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public void usrDelete(View view){
        if (!nameStr.equals("Tempting") && !nameStr.equals("Yedro")) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("cavalerie");
            reference.child(nameStr).setValue(null);

            Toast.makeText(this, "Équidé Supprimé", Toast.LENGTH_SHORT).show();

            Answers.getInstance().logCustom(new CustomEvent("Removed pony")
                    .putCustomAttribute("Name", nameStr));
        }
        else{
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
