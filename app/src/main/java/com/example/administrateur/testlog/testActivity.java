package com.example.administrateur.testlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class testActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void test(View view){
        String s = ((EditText)findViewById(R.id.editTest)).getText().toString();

        try{
            if(verifDate(s)){
                Toast.makeText(this, "true !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "false !", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean verifDate(String date){
        int[][] list = new int[][] {{1,31},{2,28},{3,31},{4,30},{5,31},{6,30},{7,31},{8,31},{9,30},{10,31},{11,30},{12,31}};

        int month = Integer.parseInt(""+date.charAt(3)+date.charAt(4));
        if (month>0 && month<13){
            int day = Integer.parseInt(""+date.charAt(0)+date.charAt(1));
            int max = list[month-1][1];
            if (day>0 && day<max+1){
                return true;
            }
        }
        return false;
    }
}
