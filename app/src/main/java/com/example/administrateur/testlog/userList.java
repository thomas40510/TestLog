package com.example.administrateur.testlog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class userList extends AppCompatActivity {

    private Button btn;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private List<String> arrayList;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        auth = FirebaseAuth.getInstance();


        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        //arrayList = updateValue(arrayList);

        if (auth.getCurrentUser() == null) {
            arrayList.add("Please login to access DataBase");

            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

            // Here, you set the data in your ListView
            list.setAdapter(adapter);
        } else {
            arrayList = DBFetch.userlist;


            // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
            // and the array that contains the data
            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

            // Here, you set the data in your ListView
            list.setAdapter(adapter);


            // Fetches values from DB to display it in list

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
                Log.d("INFO", arrayList.toString());
                adapter.notifyDataSetChanged();
                //updateText(1);
                */
                    arrayList = DBFetch.userlist;
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
                    Toast.makeText(getApplicationContext(), arrayList.get(position), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    intent.putExtra("name", arrayList.get(position));
                    startActivity(intent);

                }
            });
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
                //arrayList = updateValue(arrayList);
                arrayList = DBFetch.userlist;
                adapter.notifyDataSetChanged();
                break;
            case R.id.printLs:
                print();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void print(){
        String test = "";
        for (int i = 0; i < DBFetch.userlist.size(); i++) {
            test = test.concat(DBFetch.userlist.get(i) + "\n");
        }
        generatePdf gen = new generatePdf();
        gen.createPdf(test, "Cavaliers", "userList-" + System.currentTimeMillis() + ".pdf", getApplicationContext());
        //gen.createPdf(test, "Cavaliers", "file.pdf", this);

        //createPdf(test, "cavaliers");

    }

}

