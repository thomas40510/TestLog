package com.example.administrateur.testlog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ponyList extends AppCompatActivity {

    private ListView list;
    private ArrayAdapter<String> adapter;
    private List<String> arrayList;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pony_list);

        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayList = DBFetch.clist;


        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

        // Here, you set the data in your ListView
        list.setAdapter(adapter);


        // Fetches values from DB to display it in list

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

                Log.d("INFO", arrayList.toString());
                adapter.notifyDataSetChanged();
                //updateText(1);
                */
                arrayList = DBFetch.clist;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), PonyProfile.class);
                intent.putExtra("name", arrayList.get(position));
                startActivity(intent);

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
        inflater.inflate(R.menu.ponylist_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                DBFetch fetch = new DBFetch();
                fetch.fetchDB();
                arrayList = DBFetch.clist;
                adapter.notifyDataSetChanged();
                break;
            case R.id.print:
                print();
                break;
            case R.id.printRations:
                new MyAsyncTask().execute();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void print(){
        String text ="";
        for (int i=0; i<DBFetch.clist.size(); i++){
            text = text.concat(DBFetch.clist.get(i) + "\n");
        }
        generatePdf gen = new generatePdf();
        gen.createPdf(text, "Cavalerie", "caval-"+System.currentTimeMillis()+".pdf", getApplicationContext());
    }

    public String[][] rations = new String[DBFetch.clist.size()][4];

    public void printRations(){
        generatePdf gen = new generatePdf();
        gen.createTablePdf(rations, "Rations", "test-" + System.currentTimeMillis() + ".pdf", getApplicationContext());
    }

    public String[][] getRations() {
        DBFetch.clist.remove("Tempting");

        for (final String name : DBFetch.clist) {
            final int index = DBFetch.clist.indexOf(name);
            rations[index][0] = name;
            System.out.println("" + index);
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myref = db.getReference("cavalerie");
            DatabaseReference nameRef = myref.child(name).child("ration");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    rations[index][1] = dataSnapshot.child("mt").getValue(String.class);
                    rations[index][2] = dataSnapshot.child("md").getValue(String.class);
                    rations[index][3] = dataSnapshot.child("s").getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            System.out.println("r : " + rations[index][1]);
        }

        return rations;
    }

    AlphaAnimation inAnimation, outAnimation;
    FrameLayout progressBarHolder;


    //AsyncTask to make sure to get values from DB
    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
            inAnimation = new AlphaAnimation(0f,1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(Void... params) {
            rations = getRations();
            SystemClock.sleep(500);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            outAnimation = new AlphaAnimation(1f,0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            printRations();
        }
    }
}
