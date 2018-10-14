package com.vrem.wifianalyzer.udptransfer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpclient {
    public void send() throws Exception{
        // get server address, i just put localhost as place holder
        InetAddress serverADDR = InetAddress.getLocalHost();
        int PORT = 4000;

        // create socket
        DatagramSocket s = new DatagramSocket();
        // packet
        byte[] buf = new byte[1000];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);

        while(true){
            DatagramPacket out = new DatagramPacket(buf,buf.length,serverADDR, PORT);
            s.send(out);
        }
    }
}