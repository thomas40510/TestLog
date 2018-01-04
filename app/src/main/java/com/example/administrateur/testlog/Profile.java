package com.example.administrateur.testlog;

import android.content.ClipboardManager;
import android.content.Context;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    public View view, view2;
    public TextView bDate;
    public TextView address;
    public TextView flechage;
    public TextView forfait;
    public  TextView remaining;
    public TextView licence;
    public static String nameStr,bDateStr,addressStr,cityStr,flechStr,forfaitStr,remainStr,licStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bDate = (TextView) findViewById(R.id.bDate);
        address = (TextView) findViewById(R.id.address);
        flechage = (TextView) findViewById(R.id.flechage);
        remaining = (TextView) findViewById(R.id.remaining);
        forfait = (TextView) findViewById(R.id.forfait);
        licence = (TextView) findViewById(R.id.licence);

        Bundle extras = getIntent().getExtras();
        nameStr = extras.getString("name");

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(nameStr);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
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


                bDate.setText(bDateStr);
                address.setText(addressStr+", "+cityStr);
                flechage.setText(flechStr);
                forfait.setText(forfaitStr);
                remaining.setText(remainStr);
                licence.setText(licStr);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        findViewById(R.id.cpIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(licStr);
                Toast.makeText(getApplicationContext(), "N° de licence copié dans le presse-papiers", Toast.LENGTH_SHORT).show();

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.deleteUser:
                confirmDelete(view);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editProfile (View view){
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
        DatabaseReference reference = database.getReference("users");
        reference.child(nameStr).setValue(null);

        Toast.makeText(this, "Utilisateur Supprimé", Toast.LENGTH_SHORT).show();
        finish();
    }
}
