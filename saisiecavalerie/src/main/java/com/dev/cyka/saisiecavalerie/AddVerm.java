package com.dev.cyka.saisiecavalerie;

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
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddVerm extends AppCompatActivity {

    private ListView list;
    public List<String> arrayList;
    private List<InfoRowData> infodata;
    private List<String> selected;
    private List<Integer> rmainList;
    private int rmain;
    private String toPrintStr;
    public String[] months = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet","Août", "Septembre", "Octobre", "Novembre", "Décembre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verm);
        list = (ListView) findViewById(R.id.listView);

        //arrayList = updateValue(arrayList);
        arrayList = new ArrayList<>();
        selected = new ArrayList<>();
        rmainList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavalerie");
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
                arrayList = DBFetch.clist;
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
                        System.out.println("Selectes Are == " + arrayList.get(position));
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
        inflater.inflate(R.menu.addverm_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addPony:
                Intent intent = new Intent(this, NewPony.class);
                startActivity(intent);
                break;
            case R.id.refresh:
                //updateValue();
                arrayList = DBFetch.clist;
                break;
            //adapter.notifyDataSetChanged();
            /**
            case R.id.printLs:

                toPrintStr = "";

                FirebaseDatabase dbase = FirebaseDatabase.getInstance();
                DatabaseReference mref = dbase.getReference("users");
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
             */
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
        DatabaseReference myref = database.getReference("cavalerie");
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
                        vermDetails();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public void vermDetails() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vermifuge")
                .setMessage("Entrer les informations sur le vermifuge.");

        final EditText vermName = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        vermName.setLayoutParams(lp);
        vermName.setHint("nom du vermifuge");
        vermName.setSingleLine();
        vermName.setImeOptions(EditorInfo.IME_ACTION_DONE);


        final DatePicker picker = new DatePicker(this);
        picker.setCalendarViewShown(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(vermName);
        layout.addView(picker);

        builder.setView(layout);

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String vermNameStr = vermName.getText().toString();
                String dateStr = "";
                //dateStr = dateStr.concat(picker.getYear() + "/").concat(months[picker.getMonth()] + "/").concat(picker.getDayOfMonth() + "");
                dateStr = dateStr.concat(picker.getYear()+"-"+picker.getMonth()+"-"+picker.getDayOfMonth());
                saveVerm(vermNameStr, dateStr);
                Log.e("date", dateStr);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void saveVerm(final String verNameStr, final String dateStr){
        FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavalerie");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                writeValues(verNameStr, dateStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //ref.setValue(rmain);
        Log.e("DBG", "" + rmain);


    }


    public void writeValues(String vermNameStr, String dateStr){
        FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavalerie");
        for (int i = 0; i<selected.size();i++){
            mref.child(selected.get(i)).child("lastverm").child(dateStr).setValue(vermNameStr);
        }

        Toast.makeText(this, "Changements enregistrés !", Toast.LENGTH_LONG).show();
        selected.clear();
        finish();
    }
}
