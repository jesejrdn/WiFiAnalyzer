
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ServerFragment extends Fragment {
    TextView wait;
    private static final int socketServerPORT = 8080;
    ProgressBar prog;
    String message = "";
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recieve_content, container, false);
        Button receive_button = view.findViewById(R.id.tcp_receive);
        Button udp_receive = view.findViewById(R.id.udp_receive);
        final TextView IPaddress = view.findViewById(R.id.ipaddr);
        // final TextView packetRatio = view.findViewById(R.id.packet_ratio);
        //  final TextView packetsLost = view.findViewById(R.id.packets_lost);
        // final TextView percentLost = view.findViewById(R.id.percent_lost);
        //wait = view.findViewById(R.id.waitingId);
        //prog = view.findViewById(R.id.progbar);
        final GraphView graph = (GraphView) view.findViewById(R.id.udp_graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);

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
                    double totalPackets = 1853;
                    double packetCount = 0;
                    String packetRatioString = "";

                    double totalPacketsLost = 0;
                    String totalPacketsLostString = "";

                    double packetLossPercentage = 0.0000;
                    String packetLossPercentString = "";

                    @Override
                    public void run() {

                        try {
                            Log.d("Serv", "You are opening a server port");
                            DatagramSocket sk = new DatagramSocket(socketServerPORT);
                            sk.setSoTimeout(2000);
                            // buffer
                            byte[] buf = new byte[100];
                            DatagramPacket dgp = new DatagramPacket(buf, buf.length);
                            Log.d("Setup", "Created datagram socket and packet");
                            for(int i = 0 ;i < 10; i++){
                                try {
                                    packetCount = 0;
                                    while (true) {
                                        sk.receive(dgp);
                                        packetCount++;
                                    }
                                } catch (SocketTimeoutException e) {
                                    Log.d("UDP SERVER TIMEOUT", "timeout catch block");
                                    Log.d("PACKET COUNT", "count:" + packetCount);


                                    //packetRatioString = "Packet Ratio: " + packetCount + "/" + totalPackets + " Packets Received";

                                /*packetRatio.post(new Runnable() {
                                    public void run() {
                                        packetRatio.setText(packetRatioString);
                                        packetRatio.setVisibility(View.VISIBLE);
                                    }
                                });*/

                                    totalPacketsLost = totalPackets - packetCount;
                                    //totalPacketsLost = ((int) totalPacketsLost);
                                    //totalPacketsLostString = "Total Packets Lost: " + totalPacketsLost + " Packets";

                                /*packetsLost.post(new Runnable() {
                                    public void run() {
                                        packetsLost.setText(totalPacketsLostString);
                                        packetsLost.setVisibility(View.VISIBLE);
                                    }
                                });*/

                                    packetLossPercentage = (1.00 - (packetCount / totalPackets)) * 100.00;
                                    //packetLossPercentage = ((int) packetLossPercentage);
                                    //packetLossPercentString = "Percent of Packets Lost: " + packetLossPercentage + "%";
                                /*percentLost.post(new Runnable() {
                                    public void run() {
                                        percentLost.setText(packetLossPercentString);
                                        percentLost.setVisibility(View.VISIBLE);
                                    }
                                });*/

                                    graph.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            addEntry(packetLossPercentage);
                                        }
                                    });

                                }
                            }

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
    // add data to graph
    private void addEntry(double packetLossPercentage) {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, packetLossPercentage), true, 10);
    }
}
