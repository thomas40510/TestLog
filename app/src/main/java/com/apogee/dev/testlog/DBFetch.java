package com.apogee.dev.testlog;

import android.app.Activity;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by creation on 02/01/2018.
 */

public class DBFetch extends Activity {

    public static List<String> userlist = new ArrayList<>();
    public static String toRenewStr;
    private List<String> fetchedList = new ArrayList<>();
    private SharedPreferences prefs;

    private int i = 0;

    public void fetchDB (final boolean dorenew, boolean fromMain){
        toRenewStr = "";

        if (fromMain) {
            prefs = MainActivity.prefs;
        } else {
            prefs = rMainActivity.prefs;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavaliers");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                String[] savedArray = loadArray("array");
                //Log.e("DBG", "loadArray.size "+savedArray.length);
                //Log.e("DBG", "childrenCount"+dataSnapshot.getChildrenCount());
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
                //Log.d("INFO", userlist.toString()+userlist.size());

                /*
                Pour afficher à chaque update de la DB
                */
                if(dorenew){
                    i++;
                    fetchRenew();
                }


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

    public void fetchRenew(){

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        FirebaseDatabase dbase = FirebaseDatabase.getInstance();
        DatabaseReference mref = dbase.getReference("cavaliers");
        if (i==1) {
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (String s : userlist) {
                        DataSnapshot snapshot = dataSnapshot.child(s).child("remainH");
                        String remainStr = snapshot.getValue(String.class);
                        if (Arrays.asList(new String[] {"FT","FC"}).contains(dataSnapshot.child(s).child("Forfait").getValue(String.class))){
                            try {
                                Date renewDate = format.parse(remainStr+"-01");
                                System.out.println("HEY !! "+renewDate);
                                Calendar cal = Calendar.getInstance();
                                Date today = new Date();
                                cal.setTime(renewDate);
                                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+3);
                                Date expDate = cal.getTime();

                                if (expDate.before(today)){
                                    //Toast.makeText(DBFetch.this, "it worked !! "+s, Toast.LENGTH_SHORT).show();
                                    toRenewStr = toRenewStr.concat(s + " (" + format.format(expDate) + ")" + "\n");
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                Crashlytics.logException(e);
                                toRenewStr = toRenStr.concat(s+" ERROR");

                            }
                        } else {
                            int remains = Integer.parseInt(snapshot.getValue().toString());
                            if (remains <= 0) {
                                toRenewStr = toRenewStr.concat(s + " (" + remains + ")" + "\n");
                            }
                        }
                    }
                    //Log.e("DBG", "showing renew");
                    MainActivity showNew = new MainActivity();
                    showNew.showRenew();
                    toRenewStr = "";

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    public String[] loadArray(String arrayName) {

        int size = prefs.getInt("usrList_size", 0);
        //Log.e("DBG", "size "+size);
        String array[] = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = prefs.getString("usr" + "_" + i, null);
            ////Log.i("DBG fetchDB @ ln66", array[i]);
        }
        return array;
    }

    public void listSave(List<String> list){
    SharedPreferences.Editor editor = prefs.edit();
    //Log.e("DBG", "list size "+list.size());
    for (int i = 0; i<list.size(); i++){
        editor.putString("usr"+"_"+i, list.get(i));
        //Log.e("DBG fetchDB @ ln75", list.get(i));
    }
    editor.putInt("usrList_size", list.size());
    editor.commit();
    }

    public static List<String> clist = new ArrayList<>();
    public static List<String> clistPrt = new ArrayList<>();
    public static String toRenStr;
    SharedPreferences cprefs;


    public void cfetchDB(boolean fromMain){

        if (fromMain) {
            cprefs = cMainActivity.prefs;
        } else {
            cprefs = eMainActivity.prefs;
        }

        toRenStr = "";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("cavalerie");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clist.clear();
                String[] savedArray = cloadArray("array");
                //Log.e("DBG", "loadArray.size "+savedArray.length);
                //Log.e("DBG", "childrenCount"+dataSnapshot.getChildrenCount());
                if (savedArray.length != dataSnapshot.getChildrenCount()){
                    for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                        String user = postsnapshot.getKey();
                        clist.add(user);
                    }
                    clistSave(clist);
                }
                else {
                    Collections.addAll(clist, savedArray);
                }
                //Log.d("INFO", clist.toString()+ clist.size());
                clistPrt.addAll(clist);
                clistPrt.remove("Tempting");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public String[] cloadArray(String arrayName) {
        int size = cprefs.getInt("usrList_size", 0);
        //Log.e("DBG", "size " + size);
        String array[] = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = cprefs.getString("pony" + "_" + i, null);
            ////Log.i("DBG fetchDB @ ln66", array[i]);
        }
        return array;
    }

    public void clistSave(List<String> list){
        SharedPreferences.Editor editor = cprefs.edit();
        //Log.e("DBG", "list size "+list.size());
        for (int i = 0; i<list.size(); i++){
            editor.putString("pony"+"_"+i, list.get(i));
            //Log.e("DBG fetchDB @ ln75", list.get(i));
        }
        editor.putInt("usrList_size", list.size());
        editor.commit();
    }

}
