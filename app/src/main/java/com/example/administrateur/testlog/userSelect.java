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

public class userSelect extends AppCompatActivity {

    private ListView list;
    public List<String> arrayList;
    private List<InfoRowData> infodata;
    private List<String> selected;
    private List<Integer> rmainList;
    private int rmain;
    private String toPrintStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        list = (ListView) findViewById(R.id.listView);

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
                arrayList = DBFetch.userlist;
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
                        decrement();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    /**
    public void decrement() {
        final FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavaliers");
        for (String s : selected) {
            final DatabaseReference myref = mref.child(s);
            myref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    rmain = Integer.parseInt(dataSnapshot.child("remainH").getValue().toString());
                    myref.child("remainH").setValue(String.valueOf(rmain-1));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //ref.setValue(rmain);
            Log.e("DBG", "" + rmain);
        }
        Toast.makeText(this, "done !", Toast.LENGTH_LONG).show();
        selected.clear();
        finish();
    }
     */
    public void decrement() {
        rmainList.clear();

        FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavaliers");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String s : selected) {
                    Log.e("DBG @ln281", dataSnapshot.child(s).child("remainH").getValue().toString());
                    rmain = Integer.parseInt(dataSnapshot.child(s).child("remainH").getValue().toString());
                    rmainList.add(rmain - 1);
                }
                writeValues();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //ref.setValue(rmain);
        Log.e("DBG", "" + rmain);


    }


    public void writeValues(){
        FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavaliers");
        for (int i = 0; i<selected.size();i++){
            mref.child(selected.get(i)).child("remainH").setValue(rmainList.get(i).toString());
        }

        Toast.makeText(this, "Changements enregistrés !", Toast.LENGTH_LONG).show();
        selected.clear();
        finish();
    }
}
