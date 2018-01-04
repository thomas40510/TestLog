package com.example.administrateur.testlog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.itextpdf.text.PageSize.A4;

/**
 * Created by creation on 04/01/2018.
 */

public class generatePdf extends AppCompatActivity {

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createPdf(String text, String titleText, String fName, Context context) {

        /*
        Checks if apps has the permission to read / write to local storage
         */
        int check = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check == PackageManager.PERMISSION_GRANTED) {
            Log.i("PermissionManager", "write granted");
        } else {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1024);
        }

        int check2 = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (check2 == PackageManager.PERMISSION_GRANTED){
            Log.i("PermissionManager", "read granted");
        }
        else {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1024);
        }

        /*
        Begin document generation
         */
        Document doc = new Document();
        String fileName = fName;

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir/generated";

            File dir = new File(path);
            if (!dir.exists()) {
                Boolean bool = dir.mkdirs();
            }

            File file = new File(dir, fileName);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();
            doc.setPageSize(A4);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd@HH:mm:ss", Locale.FRANCE);
            String currentDateandTime = sdf.format(new Date());


            Paragraph p1 = new Paragraph(text, FontFactory.getFont(FontFactory.COURIER));
            p1.setAlignment(Paragraph.ALIGN_LEFT);

            Paragraph title = new Paragraph(titleText, FontFactory.getFont(FontFactory.COURIER, 16f));
            //Font titleFont = new Font(Font.FontFamily.COURIER, 16f);
            //title.setFont(titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph genTime = new Paragraph("(generated " + currentDateandTime + ")\n\n\n", FontFactory.getFont(FontFactory.COURIER,10f));
            genTime.setAlignment(Paragraph.ALIGN_RIGHT);


            //add paragraph to document
            doc.add(title);
            doc.add(genTime);
            doc.add(p1);

            doc.addAuthor("TestLog auto-gen for CE de Sauveterre");

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            doc.close();
            //Toast.makeText(this, "created", Toast.LENGTH_SHORT).show();
            Log.e("DBG", "created");

        }

        viewPdf(fileName, "Dir/generated", context);
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory, Context context) {

        File pdfFile = new File(Environment.getExternalStorageDirectory()+ "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);


        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(pdfIntent);

        } catch (ActivityNotFoundException e) {
            //Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
            Log.e("DBG", "error reading pdf");
        }

    }
}
