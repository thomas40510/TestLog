package com.apogee.dev.testlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("users");

        SharedPreferences preferences = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);
        final String userMail = preferences.getString("lastMail", null);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.userName)).setText(dataSnapshot.child(userMail.replace("@", "_at_").replace(".", "_dot_")).getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        ((TextView) findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME);

        final TextView webstatus  = (android.widget.TextView) findViewById(R.id.webstatus);

        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

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
        gen.createTablePdf(null, "TestTable", "test"+System.currentTimeMillis()+".pdf", getApplicationContext());
    }
}
