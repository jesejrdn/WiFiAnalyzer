package com.vrem.wifianalyzer.tcptransfer.client;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class tcpclient extends AsyncTask<String,String,String> {
    private  final int socketServerPORT = 8080;
    @Override
    public  String doInBackground(String... param){
        this.sendFile(param[0],param[1]);
        return "test";
    }
    public void sendFile(String filePath, String IP){
        File sending= new File(filePath);
        byte[] fileInBytes= new byte[4096];
        int count;
        try {
            /*
            * create data inputs for the tcp client to read and write data to other person
             */
            Socket client= new Socket(IP,socketServerPORT);
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
    }
}
