package com.vrem.wifianalyzer.tcptransfer.server;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.vrem.wifianalyzer.R;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class tcpServer extends AsyncTask<String, String, Void> {


    private String message = "";
    private static final int socketServerPORT = 8080;

    @Override
    public Void doInBackground(String... f){
        this.send();
        return null;
    }
    public void send() {
        ServerSocket serverSocket;
        int count = 0;
        
        try {
            serverSocket = new ServerSocket(socketServerPORT);

            while (true) {
                Socket socket = serverSocket.accept();
                count++;
                message += "#" + count + " from "
                        + socket.getInetAddress() + ":"
                        + socket.getPort() + "\n";

                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                        socket, count);
                socketServerReplyThread.run();

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public int getPort() {
        return socketServerPORT;
    }


    public void closeSocket(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Server, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong!\n" + e.toString() + "\n";
            }

        }

    }

    public static String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += " Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}