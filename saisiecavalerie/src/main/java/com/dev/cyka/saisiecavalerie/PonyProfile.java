package com.dev.cyka.saisiecavalerie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PonyProfile extends AppCompatActivity {

    public View view, view2;
    public TextView bDate;
    public TextView proprio;
    public TextView sexe;
    public TextView lastNico;
    public TextView rationMt;
    public TextView rationMd;
    public TextView rationN;
    public TextView limhr;

    public static String nameStr,bDateStr,proprioStr,sexeStr, nicoStr, limhrStr;
    public static String[] rationStr = {"N/A", "N/A", "N/A"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pony_profile);

        bDate = (TextView) findViewById(R.id.bDate);
        proprio = (TextView) findViewById(R.id.proprio);
        sexe = (TextView) findViewById(R.id.flechage);
        lastNico = (TextView) findViewById(R.id.lastnico);
        rationMt = (TextView) findViewById(R.id.rationMt);
        rationMd = (TextView) findViewById(R.id.rationMd);
        rationN = (TextView) findViewById(R.id.rationS);
        limhr = (TextView) findViewById(R.id.limhr);

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
                nicoStr = dataSnapshot.child("lastNico").getValue(String.class);
                limhrStr = dataSnapshot.child("limhr").getValue(String.class);

                rationStr[0] = dataSnapshot.child("ration").child("mt").getValue(String.class);
                rationStr[1] = dataSnapshot.child("ration").child("md").getValue(String.class);
                rationStr[2] = dataSnapshot.child("ration").child("s").getValue(String.class);


                bDate.setText(bDateStr);
                proprio.setText(proprioStr);
                sexe.setText(sexeStr);
                lastNico.setText(nicoStr);
                limhr.setText(limhrStr);

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
     *
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
    */
}
