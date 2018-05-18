package com.example.administrateur.testlog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    public void createTablePdf(String[][] rowContent, String titleText, String fName, Context context){
        Document doc = new Document();
        PdfWriter docWriter = null;
        String fileName = fName;
        try{
            Font bf12 = new Font(Font.FontFamily.COURIER, 12);

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir/generated";

            File dir = new File(path);
            if (!dir.exists()) {
                Boolean bool = dir.mkdirs();
            }

            File file = new File(dir, fileName);
            //file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();
            doc.setPageSize(A4);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd@HH:mm:ss", Locale.FRANCE);
            String currentDateandTime = sdf.format(new Date());

            Paragraph title = new Paragraph(titleText, FontFactory.getFont(FontFactory.COURIER, 16f));
            //Font titleFont = new Font(Font.FontFamily.COURIER, 16f);
            //title.setFont(titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);

            Paragraph genTime = new Paragraph("(generated " + currentDateandTime + ")\n\n\n", FontFactory.getFont(FontFactory.COURIER,10f));
            genTime.setAlignment(Paragraph.ALIGN_RIGHT);

            PdfPTable genTable = createFirstTable(rowContent);

            doc.add(title);
            doc.add(genTime);
            doc.add(genTable);

        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            doc.close();
            Log.e("DBG", "Created");
        }
        viewPdf(fileName, "Dir/generated", context);
    }
    public static PdfPTable createFirstTable(String[][] rowContent){
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(90f);

        Font bfBold = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
        Font bf = new Font(Font.FontFamily.COURIER, 12);

        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Équidé", bfBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Matin", bfBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Midi", bfBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Soir", bfBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        for (int i=0; i<rowContent.length; i++){
            for(String text : rowContent[i]){
                cell = new PdfPCell(new Phrase(text, bf));
                if (Arrays.asList(rowContent[i]).indexOf(text)!=0){
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                table.addCell(cell);
            }
        }

        return table;
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory, Context context) {

        File pdfFile = new File(Environment.getExternalStorageDirectory()+ "/" + directory + "/" + file);
        Uri path = FileProvider.getUriForFile(context, "com.example.administrateur.testlog.fileprovider",pdfFile);


        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(pdfIntent);

        } catch (ActivityNotFoundException e) {
            //Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
            Log.e("DBG", "error reading pdf");
        }
    }
}
