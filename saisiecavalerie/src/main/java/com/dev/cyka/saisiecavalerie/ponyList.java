package com.dev.cyka.saisiecavalerie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ponylist);

        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayList = DBFetch.userlist;


        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

        // Here, you set the data in your ListView
        list.setAdapter(adapter);


        // Fetches values from DB to display it in list

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
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
                Intent intent = new Intent(getApplicationContext(), PonyProfile.class);
                intent.putExtra("name", arrayList.get(position));
                startActivity(intent);

            }
        });
    }
}
