package com.vrem.wifianalyzer.tcptransfer.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class tcpclient {
    private static final int socketServerPORT = 8080;
    public void sendfile(String filePath){
        File sending= new File(filePath);
        int size= (int) sending.length();
        byte[] fileInBytes= new byte[size];
        int count;
        try {
            Socket client= new Socket("localhost",socketServerPORT);
            DataInputStream input=new DataInputStream ( new BufferedInputStream ( client.getInputStream()));
            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            while((count=input.read(fileInBytes))>0){

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
