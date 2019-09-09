/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainMenu extends AppCompatActivity {

    int i = 0;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static String loggedUserName;
    public static String userMail;

    //Remote Config keys
    private static final String UPDATE_LINK = "update_link";

    private ListView list;

    private ArrayAdapter<String> adapter;
    private List<String> arrayList;
    private List<String> authorList;
    private List<String> msgList;
    private List<String> dateList;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        //get Remote Config instance
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can fadeoff different config values during development.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]

        //set RemoteConfig defaults
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemote();

        //display user's name
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("users");

        SharedPreferences preferences = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);
        userMail = preferences.getString("lastMail", null);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedUserName = dataSnapshot.child(userMail.replace("@", "_at_").replace(".", "_dot_")).getValue().toString();
                ((TextView)findViewById(R.id.userName)).setText(loggedUserName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        TextView versiontxt = (TextView) findViewById(R.id.version);
        versiontxt.setText(BuildConfig.VERSION_NAME);

        versiontxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if (i == 5){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
                    builder.setTitle("Easter egg ?")
                            .setMessage("Tu pensais vraiment trouver un easter-egg ici ? On n'est pas chez Google !!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();
                    i = 0;
                }
            }
        });

        final TextView webstatus  = (TextView) findViewById(R.id.webstatus);

        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        /*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        handler.postDelayed(new Runnable(){
            public void run(){
                webstatus.setText("fetching...");
                if (!hasInternetConnection(getApplicationContext())){
                    webstatus.setText("Not working !");
                }
                else{
                    webstatus.setText("fine ;)");
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        */
        boolean internetstatus = new Launcher().isInternetWorking();
        webstatus.setText(internetstatus ? "fine ;-)" : "not working !");

        CheckMessages();
    }

    public void fetchRemote(){
        long cacheExpiration = 0;
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainMenu.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        displayUpdateDialog();
                    }
                });
        // [END fetch_config_with_callback]
    }

    public void displayUpdateDialog(){
        String updateLink = mFirebaseRemoteConfig.getString(UPDATE_LINK);
        if (!updateLink.equals("none")){
            final Uri updateUri = Uri.parse(updateLink);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Attention")
                    .setMessage("Une mise à jour est disponible pour l'application. Merci de l'installer en cliquant sur le bouton ci-dessous. \n" +
                            "Son installation n'est pas obligatoire immédiatement, mais elle est grandement recommandée.")
                    .setPositiveButton("Installer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent openUpdate = new Intent(Intent.ACTION_VIEW, updateUri);
                            startActivity(openUpdate);
                        }
                    })
                    .setNegativeButton("Plus tard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }


    /**
     * Actions for toolbar menu
     */
    @Override
    //load menu file//
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //set on-click actions//
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about:
                Intent about = new Intent(this, aboutScreen.class);
                startActivity(about);
                return true;
            case R.id.settings:
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                return false;
            case R.id.logout:
                gotoAuth();
                return false;
            case R.id.createcc:
                Intent intent = new Intent(this, CreateCc.class);
                startActivity(intent);
                return true;
            case R.id.usecc:
                Intent intent2 = new Intent(this, UseCc.class);
                startActivity(intent2);
                return false;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public void gotoMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void gotoCaval(View view){
        Intent intent = new Intent(this, cMainActivity.class);
        startActivity(intent);
    }
    public void gotoReprise(View view){
        Intent intent = new Intent(this, rMainActivity.class);
        startActivity(intent);
    }
    public void gotoStables(View view){
        Intent intent = new Intent(this, eMainActivity.class);
        startActivity(intent);
    }
    public void gotoFFE(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Accès aux sites FFE")
                .setMessage("Où allons-nous ?")
                .setPositiveButton("FFE Club SIF", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        accesFFE("club");
                    }
                })
                .setNegativeButton("FFE compet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        accesFFE("compet");
                    }
                })
                .setNeutralButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }
    public void accesFFE(String which){
        Uri uri;
        uri = Uri.parse((which.equals("club"))? "https://www.telemat.org/FFE/sif/" : "https://ffecompet.ffe.com/");

        Intent openUpdate = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(openUpdate);
    }
    public void gotoAuth (){
        Intent intent = new Intent(this, LoginActivity.class);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static boolean hasInternetConnection(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
        /*
        final Network network = connectivityManager.getActiveNetwork();
        final NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);

        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    */
    }
    public void genPdf(View view){
        generatePdf gen = new generatePdf();
        gen.createTablePdf(null, "TestTable", "test"+System.currentTimeMillis()+".pdf", MainMenu.this);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public void composeMessage(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nouveau message")
                .setMessage("Entrez votre message à laisser sur le mur :");

        final EditText msgBox = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        msgBox.setHint("Message...");
        //msgBox.setImeOptions(EditorInfo.IME_ACTION_DONE);


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(msgBox);

        builder.setView(layout);

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String msgText = msgBox.getText().toString();
                String date = sdf.format(new Date());
                String author = loggedUserName;

                writeInfos(msgText, date, author);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void writeInfos(String text, String date, String author){
        int id = Integer.parseInt(arrayList.get(0));
        id++;
        FirebaseDatabase wdb = FirebaseDatabase.getInstance();
        DatabaseReference wref  = wdb.getReference().child("users").child("messages").child(Integer.toString(id));
        wref.child("author").setValue(author);
        wref.child("body").setValue(text);
        wref.child("date").setValue(date);
        Toast.makeText(this, "Message posté !", Toast.LENGTH_SHORT).show();
    }

    public void CheckMessages(){
        auth = FirebaseAuth.getInstance();

        list = (ListView) findViewById(R.id.lstview);
        arrayList = new ArrayList<>();
        authorList = new ArrayList<>();
        dateList = new ArrayList<>();
        msgList = new ArrayList<>();

        if(auth.getCurrentUser() == null){
            arrayList.add("Please login to get messages");
            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

            // Here, you set the data in your ListView
            list.setAdapter(adapter);
        } else {
            getMessages();
        }
    }

    public void getMessages(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child("messages");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                authorList.clear();
                dateList.clear();
                msgList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    if (!postSnapshot.getKey().equals("nextID")){
                        arrayList.add(postSnapshot.getKey());
                        authorList.add(postSnapshot.child("author").getValue(String.class));
                        msgList.add(postSnapshot.child("body").getValue(String.class));
                        dateList.add(postSnapshot.child("date").getValue(String.class));
                    }
                }
                Collections.reverse(arrayList);
                Collections.reverse(authorList);
                Collections.reverse(msgList);
                Collections.reverse(dateList);
                // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
                // and the array that contains the data
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);



                // Here, you set the data in your ListView
                list.setAdapter(new Adapter());
                Toast.makeText(MainMenu.this, "got "+arrayList.size()+" messages", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                if (authorList.get(position).equals(loggedUserName) || loggedUserName.equals("Admin")){
                    AlertDialog.Builder b = new AlertDialog.Builder(MainMenu.this);
                    b.setTitle("Suppression")
                            .setMessage("Voulez-vous supprimer ce message ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (gotCode(authorList.get(position))) {
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference delref = db.getReference().child("users").child("messages");
                                        delref.child(arrayList.get(position)).setValue(null);
                                        Toast.makeText(getApplicationContext(), "Message supprimé !", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainMenu.this, "La suppression a échoué. Vérifiez vos droits ou le code saisi.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.show();
                }
                return false;
            }
        });
    }

    private String codeStr;
    private boolean gotCode(final String author){
        if (loggedUserName.equals("Admin") && !author.equals("Admin")) {
            codeStr = "000";
            AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);

            final EditText codeBox = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            codeBox.setHint("Code...");
            codeBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

            //msgBox.setImeOptions(EditorInfo.IME_ACTION_DONE);


            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(codeBox);

            builder.setView(layout);

            builder.setTitle("Suppression du message")
                    .setMessage("Admin, veuillez entrer le code de suppression.")
                    .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            codeStr = codeBox.getText().toString();
                            Looper.getMainLooper().quit();
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();

            try {
                Looper.loop();
            } catch (RuntimeException e){e.printStackTrace();};

            Log.e("DBG","code entered "+codeStr);
            return codeStr.equals("2242");

        } else{
            return true;
        }
    }

    public class Adapter extends BaseAdapter {

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
            row = View.inflate(getApplicationContext(), R.layout.message_row, null);
            TextView msgAuthor = (TextView) row.findViewById(R.id.msgAuthor);
            TextView msgText = (TextView) row.findViewById(R.id.msgText);
            TextView msgDate = (TextView) row.findViewById(R.id.msgDate);

            msgAuthor.setText(authorList.get(position));
            msgText.setText(msgList.get(position));
            msgDate.setText(dateList.get(position));


            return row;
        }
    }

    public void delMessage(){
        //TODO : implement method to delete own messages
    }
}
