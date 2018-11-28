
package com.vrem.wifianalyzer.tcptransfer.client;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vrem.wifianalyzer.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


import static android.app.Activity.RESULT_OK;

public class ClientFragment extends Fragment implements tcpinterface {
    private static final int PICKFILE_RESULT_CODE = 8778;
    private Button button1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_content, container, false);

        final EditText ip = view.findViewById(R.id.address);
        Button button = view.findViewById(R.id.FiletoSend);

        final TextView oneWayLatency = view.findViewById(R.id.latency);
        final TextView oneWaythroughput = view.findViewById(R.id.throughput);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(Intent.ACTION_GET_CONTENT);
                inte.setType("*/*");
                inte = Intent.createChooser(inte, "Choose a file");
                startActivityForResult(inte, PICKFILE_RESULT_CODE);

            }
        });

        button1 = view.findViewById(R.id.Submittcp);
        button1.setOnClickListener(new View.OnClickListener() {

            /* supposed to execute the file transfer but it doesn't?*/

            @Override
            public void onClick(View view) {

                final String realip = ip.getText().toString();
                final long timee;
                new Thread(new Runnable() {
                    File sending;

                    @Override
                    public void run() {
                        byte[] fileInBytes = new byte[100];
                        int count;

                        double throughput = 0;
                        try {
                            /*
                             * create data inputs for the tcp client to read and write data to other person
                             */
                            Log.i("IN", realip);
                            Socket client = new Socket(realip, 8080);
                            Log.i("IN", "message");
                            AssetManager asst = getActivity().getAssets();
                            InputStream input = asst.open("test.pdf");

                            OutputStream output = client.getOutputStream();
                            long time = System.nanoTime();
                            while ((count = input.read(fileInBytes)) > 0) {
                                /*
                                send the data over in chunks
                                 */
                                output.write(fileInBytes, 0, count);
                                Log.i("INT", "data being sent");
                            }
                            long result = System.nanoTime() - time;
                            double sr=result;
                            double sresult =(sr/ 1000000000);
                            Log.i("TIME TAKEN", Long.toString(result) + "s");
                            throughput = 1.16 / sresult;
                            output.close();
                            input.close();
                            client.close();
                            Log.e("CLIENT", "FINISHED SUCCESSFULLY");

                            final String latencyString = Double.toString(sresult);
                            final String throughputString = Double.toString(throughput);

                            oneWayLatency.post(new Runnable() {
                                @Override
                                public void run() {
                                    oneWayLatency.setText(" Latency: " + latencyString + "s");
                                    oneWayLatency.setVisibility(View.VISIBLE);
                                }
                            });
                            oneWaythroughput.post(new Runnable() {
                                @Override
                                public void run() {
                                    oneWaythroughput.setText("Throughput: " + throughputString+ "Mbs");
                                    oneWaythroughput.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        Button button2 = view.findViewById(R.id.Submitudp);
        button2.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                final String realip = ip.getText().toString();
                oneWayLatency.setText(realip);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] fileInBytes = new byte[4096];
                        int packetCount = 0;
                        try {
                            int PORT = 8080;

                            // create socket
                            DatagramSocket client = new DatagramSocket();

                            // empty packet
                            byte[] buf = new byte[100];

                            DatagramPacket packet;
                            AssetManager asst=getActivity().getAssets();
                            for (int i = 0; i < 100; i++) {

                                Log.d("UDP Client", "In for loop");
                                InputStream input = asst.open("test.jpg");
                                while (input.read(buf) != -1) {
                                    packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(realip), PORT);
                                    client.send(packet);
                                    packetCount++;
                                }
                                Log.d("UDP Client", "SENT PACKET "+packetCount);
                                input.close();
                                Thread.sleep(500);
                            }
                            Log.d("UDP Client", "SENT PACKET " + packetCount);
                            client.close();
                            Log.d("UDP Client Closed", "End Transmission");
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                TextView s = getView().findViewById(R.id.Filname);
                s.setText(Data.getData().getPath());
//                file = Data.getData().toString();
            }
        }
    }

    @Override
    public void publish_results(String test) {

    }
}