package com.example.administrateur.testlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class userSelect extends AppCompatActivity {

    private ListView list;
    public List<String> arrayList;
    private List<InfoRowData> infodata;
    private List<String> selected;
    private List<Integer> rmainList;
    private int rmain;
    private String toPrintStr;
    public String date;

    private String whatsNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        list = (ListView) findViewById(R.id.listView);

        /*
        try {
            Bundle extras = getIntent().getExtras();
            decrement = extras.getBoolean("decrement");
        } catch (Exception e){
            e.printStackTrace();
            decrement = true;
        }*/

        try{
            Bundle extras = getIntent().getExtras();
            whatsNext = extras.getString("whatsNext");
        } catch (Exception e){
            e.printStackTrace();
        }

        //arrayList = updateValue(arrayList);
        arrayList = new ArrayList<>();
        selected = new ArrayList<>();
        rmainList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavaliers");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                arrayList.clear();
                for (DataSnapshot postsnapshot: dataSnapshot.getChildren()){
                    String user = postsnapshot.getKey();
                    arrayList.add(user);
                }
                */
                switch (whatsNext){
                    case "addUser":
                        arrayList.clear();
                        for (String s : DBFetch.userlist){
                            if (!dataSnapshot.child(s).child("isAssigned").getValue(Boolean.class)){
                                arrayList.add(s);
                            }
                        }
                        break;
                    case "delUser":
                        arrayList.addAll(repInfo.cavalList);
                        break;
                    default :
                        arrayList.addAll(DBFetch.userlist);
                        break;
                }
                Log.d("INFO", arrayList.toString());
                Log.e("DEBUG", "" + arrayList.size());

                Log.e("DBG", "reached !");
                infodata = new ArrayList<InfoRowData>();
                for (int i = 0; i < arrayList.size(); i++) {
                    infodata.add(new InfoRowData(false, i));
                    // System.out.println(i);
                    //System.out.println("Data is == "+data[i]);
                }
                list.invalidate();
                list.setAdapter(new MyAdapter());


                //updateText(1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.e("DBG", "reached !");
        infodata = new ArrayList<InfoRowData>();
        for (int i = 0; i < arrayList.size(); i++) {
            infodata.add(new InfoRowData(false, i));
            // System.out.println(i);
            //System.out.println("Data is == "+data[i]);
        }
        list.invalidate();
        list.setAdapter(new MyAdapter());


        //updateValue();
        //list.setAdapter(new MyAdapter());
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View row = null;
            row = View.inflate(getApplicationContext(), R.layout.list_row, null);
            TextView tvContent = (TextView) row.findViewById(R.id.tvContent);
            //tvContent.setText(data[position]);
            tvContent.setText(arrayList.get(position));
            //System.out.println("The Text is here like.. == "+tvContent.getText().toString());

            final CheckBox cb = (CheckBox) row
                    .findViewById(R.id.chbContent);
            cb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (infodata.get(position).isclicked) {
                        infodata.get(position).isclicked = false;
                    } else {
                        infodata.get(position).isclicked = true;
                    }

                    //for (int i = 0; i < infodata.size(); i++) {
                    if (infodata.get(position).isclicked) {
                        System.out.println("Selected Are == " + arrayList.get(position));
                        selected.add(arrayList.get(position));
                        //}
                    } else {
                        selected.remove(arrayList.get(position));
                    }
                }
            });

            if (infodata.get(position).isclicked) {

                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
            return row;
        }
    }


    /**
     * Actions for toolbar menu
     */
    @Override
    //load menu file//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.userlist_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addUser:
                Intent intent = new Intent(this, CreateProfile.class);
                startActivity(intent);
                break;
            case R.id.refresh:
                //updateValue();
                arrayList = DBFetch.userlist;
                break;
            //adapter.notifyDataSetChanged();
            case R.id.printLs:

                toPrintStr = "";

                FirebaseDatabase dbase = FirebaseDatabase.getInstance();
                DatabaseReference mref = dbase.getReference("cavaliers");
                mref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (String s : arrayList) {
                            String rStr = dataSnapshot.child(s).child("remainH").getValue().toString();
                            toPrintStr = toPrintStr.concat(s + "  :   " + rStr + "\n");
                        }
                        generatePdf gen = new generatePdf();
                        gen.createPdf(toPrintStr, "Relevé des séances restantes", "cards-" + System.currentTimeMillis() + ".pdf", getApplicationContext());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //ref.setValue(rmain);
                Log.e("DBG", "" + rmain);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    /**
     * Fetches users from DB
     */
    public void updateValue (){
        arrayList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavaliers");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot postsnapshot: dataSnapshot.getChildren()){
                    String user = postsnapshot.getKey();
                    arrayList.add(user);
                }
                Log.d("INFO", arrayList.toString());
                Log.e("DEBUG", ""+arrayList.size());
                list.setAdapter(new MyAdapter());
                //updateText(1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    String usrList;
    public void confirm (View view){
        usrList = "";

        for (int i=0; i<selected.size(); i++){
            usrList = usrList.concat(selected.get(i)+"\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vous avez séléctionné : ")
                .setMessage(usrList)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (whatsNext.equals("decrement") || whatsNext.equals("increment")){
                            enterDate();
                        } else {
                            manUsers();
                        }
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    public void enterDate(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Date de la séance :");


        final DatePicker picker = new DatePicker(this);
        picker.setCalendarViewShown(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(picker);

        builder.setView(layout);

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                date = "";
                //dateStr = dateStr.concat(picker.getYear() + "/").concat(months[picker.getMonth()] + "/").concat(picker.getDayOfMonth() + "");
                date = date.concat(picker.getYear()+"-"+(picker.getMonth()+1)+"-"+picker.getDayOfMonth());
                if (whatsNext.equals("decrement")) {
                    decrement();
                } else {
                    enterCard();
                }
                Log.e("date", date);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void manUsers(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cref = database.getReference("cavaliers");
        DatabaseReference repref = database.getReference("reprises").child(repInfo.day).child(repInfo.reprise);

        switch (whatsNext){
            case "addUser":
                repref.child("cavaliers").setValue(selected);
                break;
            case "delUser":
                for (String s : selected){
                    arrayList.remove(s);
                }
                repref.child("cavaliers").setValue(arrayList);
                break;
        }

        for (String u : selected){
            cref.child(u).child("isAssigned").setValue(whatsNext.equals("addUser"));
        }

        Toast.makeText(this, "Opération effectuée !", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void decrement() {
        rmainList.clear();

        FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavaliers");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String histo;
                for (String s : selected) {
                    Log.e("DBG @ln281", dataSnapshot.child(s).child("remainH").getValue().toString());
                    rmain = Integer.parseInt(dataSnapshot.child(s).child("remainH").getValue().toString());
                    try {
                        histo = dataSnapshot.child(s).child("histoCarte").child(date).getValue().toString();
                    } catch (Exception e){
                        e.printStackTrace();
                        histo = null;
                    }
                    if(histo!=null){
                        int childrenCount = 0;
                        for (DataSnapshot postsnapshot : dataSnapshot.child(s).child("histoCarte").getChildren()){
                            if (postsnapshot.getKey().contains(date)){
                                childrenCount++;
                            }
                        }
                        date = date+"("+(childrenCount+1)+")";
                    }
                    rmainList.add(rmain - 1);
                }
                writeValues("1 heure prise sur la carte", "none");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //ref.setValue(rmain);
        Log.e("DBG", "" + rmain);
    }

    public void enterCard(){
        rmainList.clear();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type de carte :");


        final RadioGroup radioGroup = new RadioGroup(this);
        final RadioButton radio10 = new RadioButton(this);
        radio10.setText("Carte de 10 h");
        radio10.setId(10);
        final RadioButton radio30 = new RadioButton(this);
        radio30.setText("Carte de 30 h");
        radio30.setId(30);
        final RadioButton radioFT = new RadioButton(this);
        radioFT.setText("Forfait trimestriel");
        radioFT.setId(42);
        final RadioButton radioFC = new RadioButton(this);
        radioFC.setText("Forfait compétition");
        radioFC.setId(43);

        ArrayList<View> radioList = new ArrayList<View>();
        radioList.add(radio10);
        radioList.add(radio30);
        radioList.add(radioFT);
        radioList.add(radioFC);
        for (View v : radioList){
            radioGroup.addView(v);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(radioGroup);

        builder.setView(layout);
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final int toAdd;
                final String cardType;
                switch (radioGroup.getCheckedRadioButtonId()){
                    case 10:
                        toAdd = 10;
                        cardType = "c10";
                        break;
                    case 30 :
                        toAdd = 30;
                        cardType = "c30";
                        break;
                    case 42 :
                        toAdd = 26;
                        cardType = "FT";
                        break;
                    case 43 :
                        toAdd = 39;
                        cardType = "FC";
                        break;
                    default:
                        toAdd = 0;
                        cardType = "none";
                        break;
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mref = database.getReference("cavaliers");
                mref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String histo;
                        for (String s : selected) {
                            Log.e("DBG @ln281", dataSnapshot.child(s).child("remainH").getValue().toString());
                            rmain = Integer.parseInt(dataSnapshot.child(s).child("remainH").getValue().toString());
                            try {
                                histo = dataSnapshot.child(s).child("histoCarte").child(date).getValue().toString();
                            } catch (Exception e){
                                e.printStackTrace();
                                histo = null;
                            }
                            if(histo!=null){
                                int childrenCount = 0;
                                for (DataSnapshot postsnapshot : dataSnapshot.child(s).child("histoCarte").getChildren()){
                                    if (postsnapshot.getKey().contains(date)){
                                        childrenCount++;
                                    }
                                }
                                date = date+"("+(childrenCount+1)+")";
                            }
                            rmainList.add(rmain + toAdd);
                        }
                        writeValues(toAdd+" heures ajoutées sur la carte", cardType);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
        });
        builder.show();
    }


    public void writeValues(String histoMsg, String cardType){
        FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavaliers");
        for (int i = 0; i<selected.size();i++){
            mref.child(selected.get(i)).child("remainH").setValue(rmainList.get(i).toString());

            mref.child(selected.get(i)).child("histoCarte").child(date).setValue(histoMsg);

            if(!cardType.equals("none")){
                mref.child(selected.get(i)).child("Forfait").setValue(cardType);
            }
        }

        Toast.makeText(this, "Changements enregistrés !", Toast.LENGTH_LONG).show();
        finish();
    }
}
