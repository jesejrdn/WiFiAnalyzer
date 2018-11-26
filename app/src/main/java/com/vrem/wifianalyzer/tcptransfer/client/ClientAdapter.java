package com.vrem.wifianalyzer.tcptransfer.client;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientAdapter extends AsyncTask<String,Void,String> {
    tcpinterface tcp;
    public ClientAdapter(tcpinterface tcp){
        this.tcp=tcp;
    }
    @Override
    protected void onPreExecute(){
        Log.e("IN", "ONPRE EXECUTE");
        tcp.publish_results("ey a file is being transfered");
    }
    private  final int socketServerPORT = 8080;
    @Override
    protected String doInBackground(String... Params){
        File sending= new File(Params[0]);
        byte[] fileInBytes= new byte[4096];
        int count;
        try {
            /*
             * create data inputs for the tcp client to read and write data to other person
             */
            Socket client= new Socket(Params[1],socketServerPORT);
            InputStream input=new FileInputStream(sending);
            OutputStream output = client.getOutputStream();
            while((count=input.read(fileInBytes))>0){
            /*
            send the data over in chunks
             */
                output.write(fileInBytes,0,count);
            }
            output.close();
            input.close();
            client.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return"test";
    }
    @Override
    protected void onProgressUpdate(Void... Params){

    }
    @Override
    protected void onPostExecute(String Params){
        tcp.publish_results(Params);
    }

}
