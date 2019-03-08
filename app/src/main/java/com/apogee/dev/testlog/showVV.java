/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class showVV extends AppCompatActivity {

    private String nameStr, type, dbChildName, month;
    private Boolean isMonth;
    private List<String> dateList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private String completeList;
    private String typeStr, monthStr;

    public String[] monthList = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vv);

        Bundle extras = getIntent().getExtras();
        try {
            nameStr = extras.getString("name");
            type = extras.getString("type");
            month = extras.getString("month");

            if(month.equals("none")){
                getbyName(nameStr, type);
                isMonth = false;
            } else{
                getbyMonth(month, type);
                isMonth = true;
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Il y a eu une erreur. Merci de réessayer.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Actions for toolbar menu
     */
    @Override
    //load menu file//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showvac_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.print:
                print();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void print(){
        generatePdf gen = new generatePdf();
        if (isMonth){
            gen.createPdf(completeList, "Liste des "+typeStr+" du mois de "+monthStr, monthStr + " - " + type + System.currentTimeMillis() + ".pdf", showVV.this);
        } else {
            gen.createPdf(completeList, nameStr + " - Derniers " + typeStr, nameStr + " - " + type + System.currentTimeMillis() + ".pdf", showVV.this);
        }
    }

    public void getbyName (String name, String type){
        ////Log.e("DBG @ln31", type + "-" + nameStr);

        TextView titleTxt = (TextView) findViewById(R.id.titleTxt);

        if (type.equals("verm")) {
            dbChildName = "lastverm";
            typeStr = "vermifuges";
        } else {
            dbChildName = "lastvacs";
            typeStr = "vaccins";

        }
        titleTxt.setText(name+" - Liste des "+typeStr);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavalerie").child(name).child(dbChildName);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dateList.clear();
                nameList.clear();

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String date = postsnapshot.getKey();
                    dateList.add(date);

                    String nameofV = postsnapshot.getValue().toString();
                    ////Log.e("DBG", name);
                    nameList.add(nameofV);
                }
                //Log.d("INFO", dateList.toString() + dateList.size());
                showList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getbyMonth(final String month, String type){
        TextView titleTxt = (TextView) findViewById(R.id.titleTxt);

        monthStr = monthList[Integer.parseInt(month)-1];

        if (type.equals("verm")) {
            dbChildName = "lastverm";
            typeStr = "vermifuges";
        } else {
            dbChildName = "lastvacs";
            typeStr = "vaccins";

        }
        titleTxt.setText(typeStr+" du mois de "+monthStr);

        dateList.clear();
        nameList.clear();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("cavalerie");
        for (String s : DBFetch.clist){
            final String st = s;
            DatabaseReference myref = reference.child(s).child(dbChildName);
            myref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                        String date = postsnapshot.getKey();
                        if(date.contains("-"+month+"-")){
                            dateList.add(date);
                            nameList.add(st);
                        }
                    }
                    showList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void showList() {
        completeList = "";
        TextView listView = (TextView) findViewById(R.id.vermvacTextView);
        //for (int i = nameList.size()-1; i>-1; i--) {
        for (int i = 0; i<nameList.size(); i++){
            if (isMonth){
                completeList = completeList.concat("• "+nameList.get(i) + " ("+dateList.get(i)+")\n\n");

            } else {
                completeList = completeList.concat(dateList.get(i) + " : " + nameList.get(i) + "\n\n");
            }
        }
        ////Log.e("DBG", completeList);
        listView.setText(completeList);
    }

}
