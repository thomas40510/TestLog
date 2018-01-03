package com.example.administrateur.testlog;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> userlist = new ArrayList<>();
    int n;
    private ArrayList<String> toRenew = new ArrayList<>();
    private String toRenewStr;
    private int nbr;
    public static SharedPreferences prefs;
    private static AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder polBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(polBuilder.build());

        prefs = getSharedPreferences(shPrefs.sharedPrefs, MODE_PRIVATE);

        n = 1;
        builder = new AlertDialog.Builder(this);

        //updateValue();
        DBFetch fetch = new DBFetch();
        fetch.fetchDB();
        //showRenew();


    }

    public void updateValue () {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference("users");
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String user = postsnapshot.getKey();
                    userlist.add(user);
                    if (Integer.parseInt(dataSnapshot.child(user).child("remainH").getValue().toString())<=0){
                        toRenewStr = toRenewStr.concat(user);
                    }
                }
                Log.d("INFO", userlist.toString());
                //updateText(1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public void showRenew(){
        Log.e("DBG main@ln80", ""+DBFetch.userlist.size());
        toRenewStr = DBFetch.toRenewStr;

        if (!toRenewStr.equals("")) {
            builder.setTitle("to renew : ")
                    .setMessage(toRenewStr)
                    .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
    }

    public void display (View view){
        //Intent intent = new Intent(this, editDB.class);
        //startActivity(intent);

        createPdf("O rage, O déserspoir, O vieillesse ennemie");

    }

    public void display3 (View view){
        Intent intent = new Intent(this, userList.class);
        startActivity(intent);
    }

    public void gotoSelect(View view){
        Intent intent = new Intent(this, userSelect.class);
        startActivity(intent);
    }
    public void gotoAuth (View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createPdf(String text) {

        Document doc = new Document();
        String fileName = "File.pdf";

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir/files";

            File dir = new File(path);
            dir.mkdir();

            File file = new File(dir, fileName);
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph(text);
            Font paraFont= new Font(Font.FontFamily.COURIER);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            doc.close();
            Toast.makeText(this, "created", Toast.LENGTH_SHORT).show();

        }

        viewPdf(fileName, "Dir/generated");
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);


        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

}
