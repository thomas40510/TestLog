/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Profile extends AppCompatActivity {

    private static final String currentYear = "2020";

    public View view, view2;
    public TextView bDate;
    public TextView address;
    public TextView flechage;
    public TextView forfait;
    public  TextView remaining;
    public TextView licence;
    public TextView tel, mail,textHisto;
    public TextView adyear, licyear;
    public static String nameStr,bDateStr,addressStr,cityStr,flechStr,forfaitStr,remainStr,licStr,telStr,mailStr,telWhoStr;
    public static String numberStr,histoStr,adlic;
    private List<String> histoLst = new ArrayList<>();


    private Menu profileMenu ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String prevYear = (Integer.toString(Integer.parseInt(currentYear) - 1));

        bDate = (TextView) findViewById(R.id.bDate);
        address = (TextView) findViewById(R.id.address);
        flechage = (TextView) findViewById(R.id.flechage);
        remaining = (TextView) findViewById(R.id.remaining);
        forfait = (TextView) findViewById(R.id.forfait);
        licence = (TextView) findViewById(R.id.licence);
        tel = (TextView) findViewById(R.id.tel);
        mail = (TextView) findViewById(R.id.mail);

        adyear = (TextView)findViewById(R.id.adyear);
        licyear = (TextView) findViewById(R.id.licyear);

        Bundle extras = getIntent().getExtras();
        nameStr = extras.getString("name");

        TextView name = (TextView)findViewById(R.id.nom);
        name.setText(nameStr);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavaliers");
        DatabaseReference nameRef = myref.child(nameStr);

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bDateStr = dataSnapshot.child("Birthdate").getValue(String.class);
                addressStr = dataSnapshot.child("adresse").getValue(String.class);
                cityStr = dataSnapshot.child("ville").getValue(String.class);
                flechStr = dataSnapshot.child("fléchage").getValue(String.class);
                forfaitStr = dataSnapshot.child("Forfait").getValue(String.class);
                remainStr = dataSnapshot.child("remainH").getValue(String.class);
                licStr = dataSnapshot.child("licNbr").getValue(String.class);
                numberStr = dataSnapshot.child("tel").child("nbr").getValue(String.class);
                telWhoStr = dataSnapshot.child("tel").child("who").getValue(String.class);
                mailStr = dataSnapshot.child("mail").getValue(String.class);

                adlic = dataSnapshot.child("adlic").getValue(String.class);

                bDate.setText(bDateStr);
                address.setText(addressStr+", "+cityStr);
                flechage.setText(flechStr);
                forfait.setText(forfaitStr);
                remaining.setText(remainStr);
                licence.setText(licStr);
                try {
                    if (!telWhoStr.equals("mainWho")) {
                        telStr = numberStr.concat(" (" + telWhoStr + ")");
                    } else {
                        telStr = numberStr;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tel.setText(telStr);

                mail.setText(mailStr);

                try {
                    String range = prevYear + " " + currentYear;
                    adyear.setTextColor(getResources().getColor((range.contains(adlic)) ? R.color.adok : R.color.adexpired));
                    licyear.setTextColor(getResources().getColor((range.contains(adlic)) ? R.color.adok : R.color.adexpired));

                    adyear.setText(adlic);
                    licyear.setText(adlic);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getHisto(true);

        findViewById(R.id.cpIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(licStr);
                Toast.makeText(getApplicationContext(), "N° de licence copié dans le presse-papiers", Toast.LENGTH_SHORT).show();

            }
        });

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
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
        inflater.inflate(R.menu.profile_menu, menu); //your file name

        profileMenu = menu;

        try {
            profileMenu.getItem(R.id.renewAdlic).setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.findItem(R.id.renewAdlic).setEnabled(!adlic.equals(currentYear));
        return true;
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.deleteUser:
                confirmDelete(view);
                return true;
            case R.id.printHisto:
                printHisto();
                return true;
            case R.id.renewAdlic:
                renewAdLic();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getHisto(final Boolean show){
        histoLst.clear();
        //Log.e("DBG", "reached !");
        textHisto = (TextView) findViewById(R.id.textHisto);
        histoStr = "";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavaliers");
        DatabaseReference histoRef = myref.child(nameStr).child("histoCarte");

        histoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                histoLst.clear();
                histoStr="";
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String action = postsnapshot.getKey()+" : "+dataSnapshot.child(postsnapshot.getKey()).getValue(String.class);
                    histoLst.add(action);
                    //Log.e("DBG", action+" / "+histoStr);
                    //Log.e("DBG", ""+histoLst);
                }
                Collections.reverse(histoLst);
                for (String s:histoLst){
                    histoStr = histoStr.concat(s+"\n");
                }
                if(show) {
                    textHisto.setText(histoStr);
                }
                //Log.e("DBG",histoStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void printHisto(){
        generatePdf gen = new generatePdf();
        gen.createPdf(histoStr, nameStr+" - Historique des opérations", (nameStr.replace(" ","")).toLowerCase()+"-histo-" + System.currentTimeMillis() + ".pdf", Profile.this);
    }

    public void renewAdLic(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Confirmez-vous le renouvellement de l'adhésion et de la licence pour ce cavalier ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("cavaliers");
                        ref.child(nameStr).child("adlic").setValue(currentYear);

                        Answers.getInstance().logCustom(new CustomEvent("Adhésion / licence")
                                .putCustomAttribute("Name", nameStr).putCustomAttribute("by", MainMenu.loggedUserName));

                        Toast.makeText(getApplicationContext(), "adhésion / licence renouvelées !", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }

    public void editProfile (){
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra("bdate", bDateStr)
                .putExtra("address", addressStr)
                .putExtra("city", cityStr)
                .putExtra("forfait", forfaitStr)
                .putExtra("flechage", flechStr)
                .putExtra("remaining", remainStr);
        startActivity(intent);
    }

    public void confirmDelete (View view){
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     builder.setTitle("Confirmation...")
             .setMessage("Êtes-vous sûr de vouloir supprimer ce cavalier ? Il n'y a pas de retour en arrière possible...")
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavaliers");
        reference.child(nameStr).setValue(null);

        Answers.getInstance().logCustom(new CustomEvent("Removed user")
                .putCustomAttribute("Name", nameStr));

        Toast.makeText(this, "Utilisateur Supprimé", Toast.LENGTH_SHORT).show();
        finish();
    }
}
