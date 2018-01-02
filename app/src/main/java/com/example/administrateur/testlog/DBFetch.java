package com.example.administrateur.testlog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by creation on 02/01/2018.
 */

public class DBFetch extends Activity {

    public static List<String> userlist = new ArrayList<>();
    private List<String> fetchedList = new ArrayList<>();
    SharedPreferences prefs = MainActivity.prefs;

    public void fetchDB (){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                String[] savedArray = loadArray("array");
                Log.e("DBG", "loadArray.size "+savedArray.length);
                Log.e("DBG", "childrenCount"+dataSnapshot.getChildrenCount());
                if (savedArray.length != dataSnapshot.getChildrenCount()){
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String user = postsnapshot.getKey();
                    userlist.add(user);
                }
                listSave(userlist);
                }
                else {
                    Collections.addAll(userlist, savedArray);
                }
                Log.d("INFO", userlist.toString()+userlist.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    public String[] loadArray(String arrayName) {

        int size = prefs.getInt("usrList_size", 0);
        Log.e("DBG", "size "+size);
        String array[] = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = prefs.getString("usr" + "_" + i, null);
            Log.i("DBG fetchDB @ ln66", array[i]);
        }
        return array;
    }

    public void listSave(List<String> list){
    SharedPreferences.Editor editor = prefs.edit();
    Log.e("DBG", "list size "+list.size());
    for (int i = 0; i<list.size(); i++){
        editor.putString("usr"+"_"+i, list.get(i));
        Log.e("DBG fetchDB @ ln75", list.get(i));
    }
    editor.putInt("usrList_size", list.size());
    editor.commit();
    }


}
