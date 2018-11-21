package com.vrem.wifianalyzer.tcptransfer.server;

import android.os.AsyncTask;
import android.view.View;

import com.vrem.wifianalyzer.R;


public class tcpSeverAdapter extends AsyncTask<Void,Void,String> {
    @Override
    protected void onPreExecute(){

    }
    @Override
    protected String doInBackground(Void... Params) {
        String yes="tdst";
        tcpServer sender=new tcpServer();
        sender.send();
        return yes;
    }
    @Override
    protected void onProgressUpdate(Void... Params){

    }
    @Override
    protected void onPostExecute(String Params){

    }
}
