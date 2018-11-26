package com.vrem.wifianalyzer.udptransfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.FileInputStream;

public class udpclient implements Runnable {

    String IP;
    String filepath;

    public void send(String filepath, String IP){
        try {
            // get server address, i just put localhost as place holder
            InetAddress serverADDR = InetAddress.getByName(IP);
            int PORT = 4000;

            File file= new File(filepath);
            FileInputStream fis = null;

            // create socket
            DatagramSocket s = new DatagramSocket();
            DatagramPacket out = null;

            // packet
            byte[] buf = new byte[100];

            while(true){
                fis = new FileInputStream(file);

                while(fis.read(buf) != -1){
                    out = new DatagramPacket(buf, buf.length, serverADDR, PORT);
                    s.send(out);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run(){
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        send(filepath, IP);
    }
}