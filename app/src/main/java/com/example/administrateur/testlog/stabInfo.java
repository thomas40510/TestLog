package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class stabInfo extends AppCompatActivity {

    String where, titleText, whereRef;
    public static List<String> cavalList = new ArrayList<>();
    private String cavalListStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stab_info);

        Bundle extras = getIntent().getExtras();
        where = extras.getString("where");

        setValues();

        ((TextView)findViewById(R.id.titleTxt)).setText(titleText);

        getList();
    }

    /**
     * Actions for toolbar menu
     */
    @Override
    //load menu file//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stab_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                gotoSelect("add");
                return true;
            case R.id.remove:
                gotoSelect("remove");
                return false;
            case R.id.print:
                //TODO : générer feuille de rations pour l'écurie concernée
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void setValues(){
        switch (where){
            case "devant":
                titleText = "Écuries de devant";
                whereRef = "avant";
                break;
            case "derrière":
                titleText = "Écuries de derrière";
                whereRef = "arrière";
                break;
            case "poney":
                titleText = "Écurie poney";
                whereRef = "poneys";
                break;
            default:
                titleText = "Écurie privée";
                whereRef = "privée";
        }
    }

    public void getList(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("écuries").child(whereRef);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cavalList.clear();
                cavalListStr = "";


                for (int i = 0; i<dataSnapshot.getChildrenCount();i++){
                    cavalList.add(dataSnapshot.child(""+i).getValue().toString());
                }
                for (String s : cavalList){
                    cavalListStr = cavalListStr.concat(s+"\n");
                }

                ((TextView)findViewById(R.id.listText)).setText(cavalListStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void gotoSelect(String s){
        //TODO : create activity to select horses, and redirect to it
    }

}
