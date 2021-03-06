package com.dev.cyka.saisiecavalerie;

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



public class DBFetch extends Activity {
    public static List<String> clist = new ArrayList<>();
    public static String toRenStr;
    private List<String> fetchedList = new ArrayList<>();
    SharedPreferences prefs = MainActivity.prefs;
    private int i;

    public void fetchDB (){
        toRenStr = "";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavalerie");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clist.clear();
                String[] savedArray = loadArray("array");
                Log.e("DBG", "loadArray.size "+savedArray.length);
                Log.e("DBG", "childrenCount"+dataSnapshot.getChildrenCount());
                if (savedArray.length != dataSnapshot.getChildrenCount()){
                    for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                        String user = postsnapshot.getKey();
                        clist.add(user);
                    }
                    listSave(clist);
                }
                else {
                    Collections.addAll(clist, savedArray);
                }
                Log.d("INFO", clist.toString()+ clist.size());

                /*
                Pour afficher à chaque update de la DB
                 */
                i++;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        /* Pour ne l'afficher qu'au démarrage
        i++;
        fetchRenew();
        */

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
