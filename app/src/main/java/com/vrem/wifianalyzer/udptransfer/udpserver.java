package com.vrem.wifianalyzer.udptransfer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;

public class udpserver {
    public void receive() throws Exception{
        // get lacal host addr
        InetAddress addr = InetAddress.getLocalHost();
        // set port
        int PORT = 4000;
        // create a datagram socket bound to the specified local address
        DatagramSocket sk = new DatagramSocket(PORT, addr);

        // buffer
        byte[] buf = new byte[1000];
        DatagramPacket dgp = new DatagramPacket(buf, buf.length);

        byte[] data = new byte[10000];
        while(true){
            sk.receive(dgp);
        }
    }
}
