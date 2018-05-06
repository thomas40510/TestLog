package com.example.administrateur.testlog;

/**
 * Created by studioquaiouest on 30/12/2017.
 */

public class InfoRowData {
    public boolean isclicked=false;
    public int index;
    /*public String fanId;
    public String strAmount;*/

    public InfoRowData(boolean isclicked, int index/*,String fanId,String strAmount*/)
    {
        this.index=index;
        this.isclicked=isclicked;
        /*this.fanId=fanId;
        this.strAmount=strAmount;*/
    }
}
