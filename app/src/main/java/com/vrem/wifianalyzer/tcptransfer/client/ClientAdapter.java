package com.vrem.wifianalyzer.tcptransfer.client;

import android.os.AsyncTask;

public class ClientAdapter extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... Params){
        tcpclient client= new tcpclient();
        client.sendFile(Params[0],Params[1]);
        return "";
    }
    @Override
    protected void onProgressUpdate(Void... Params){

    }
    @Override
    protected void onPostExecute(String Params){

    }

}
