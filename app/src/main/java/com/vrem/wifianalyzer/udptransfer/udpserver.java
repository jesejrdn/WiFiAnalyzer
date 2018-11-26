package com.vrem.wifianalyzer.udptransfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;

public class udpserver implements Runnable {
    String filepath;

    public void receive(){
        try {
            // get lacal host addr
            InetAddress addr = InetAddress.getLocalHost();
            // set port
            int PORT = 4000;
            // create a datagram socket bound to the specified local address
            DatagramSocket sk = new DatagramSocket(PORT, addr);

            // buffer
            byte[] buf = new byte[100];
            DatagramPacket dgp = new DatagramPacket(buf, buf.length);

            while(true){
                sk.receive(dgp);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        receive();
    }
}
