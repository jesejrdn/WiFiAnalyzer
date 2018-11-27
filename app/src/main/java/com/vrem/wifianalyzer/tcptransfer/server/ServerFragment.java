
package com.vrem.wifianalyzer.tcptransfer.server;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vrem.wifianalyzer.R;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ServerFragment extends Fragment {
    TextView wait;
    private static final int socketServerPORT = 8080;
    ProgressBar prog;
    String message = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recieve_content, container, false);
        Button receive_button = view.findViewById(R.id.tcp_receive);
        Button udp_receive = view.findViewById(R.id.udp_receive);
        final TextView IPaddress = view.findViewById(R.id.ipaddr);
        wait = view.findViewById(R.id.waitingId);
        prog = view.findViewById(R.id.progbar);
        receive_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * tcp button is clicked
                 */
                String IP = tcpServer.getIpAddress();
                IPaddress.setText(IP);
                IPaddress.setVisibility(View.VISIBLE);
                String yes = "test";
                new Thread(new Runnable() {
                    int count = 0;

                    class SocketServerReplyThread extends Thread {

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
                            Log.e("mag", msgReply);
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

                    @Override
                    public void run() {
                        try {
                            Log.i("Serv", "You are opening a server port");
                            ServerSocket serverSocket = new ServerSocket(socketServerPORT);

                            while (true) {
                                Socket socket = serverSocket.accept();
                                Log.i("e", "socket accepted");
                                count++;
                                message += "#" + count + " from "
                                        + socket.getInetAddress() + ":"
                                        + socket.getPort() + "\n";
                                Log.i("message", message);
                                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                                        socket, count);
                                socketServerReplyThread.run();
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        udp_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*
                    udp click
                     */
                String IP = tcpServer.getIpAddress();
                IPaddress.setText(IP);
                IPaddress.setVisibility(View.VISIBLE);
                    /*
                    set up send for udp
                     */
                new Thread(new Runnable() {
                    int count = 0;
                    LinkedList totalPackets = new LinkedList();

                    @Override
                    public void run() {
                        try {
                            Log.d("Serv", "You are opening a server port");
                            DatagramSocket sk = new DatagramSocket(socketServerPORT);
                            sk.setSoTimeout(5000);
                            // buffer
                            byte[] buf = new byte[100];
                            DatagramPacket dgp = new DatagramPacket(buf, buf.length);
                            Log.d("Setup", "Created datagram socket and packet");
//                            for (int i = 0; i < 10; i++) {
                                try {
                                    while (true) {
                                        sk.receive(dgp);
                                        count++;
                                    }
                                }
                                catch (SocketTimeoutException e) {
                                    Log.d("UDP SERVER TIMEOUT", "timeout catch block");
                                    totalPackets.push(count);
                                    Log.d("PACKET COUNT", "count:"+count);
                                }
                                count = 0;
//                            }
                            sk.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return view;
    }

}
