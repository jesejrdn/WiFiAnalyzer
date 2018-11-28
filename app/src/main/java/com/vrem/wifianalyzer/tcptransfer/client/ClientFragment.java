
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
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

    private LineGraphSeries<DataPoint> seriesThroughput;
    private LineGraphSeries<DataPoint> seriesLatency;
    private int lastXThoughput = 0;
    private int lastXLatency = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_content, container, false);

        final EditText ip = view.findViewById(R.id.address);
//        Button button = view.findViewById(R.id.FiletoSend);

        final TextView oneWayLatency = view.findViewById(R.id.latency);
        final TextView oneWaythroughput = view.findViewById(R.id.throughput);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent inte = new Intent(Intent.ACTION_GET_CONTENT);
//                inte.setType("*/*");
//                inte = Intent.createChooser(inte, "Choose a file");
//                startActivityForResult(inte, PICKFILE_RESULT_CODE);
//
//            }
//        });

        lastXLatency = 0;
        lastXThoughput = 0;

        final GraphView latencyGraph = (GraphView) view.findViewById(R.id.latency_graph);
        latencyGraph.getViewport().setXAxisBoundsManual(true);
        latencyGraph.getViewport().setScrollable(true);

        latencyGraph.removeAllSeries();
        seriesLatency = new LineGraphSeries<DataPoint>();
        latencyGraph.addSeries(seriesLatency);
        latencyGraph.setTitle("One Way Latency");

        GridLabelRenderer latencyGridLabel = latencyGraph.getGridLabelRenderer();
        latencyGridLabel.setHorizontalAxisTitle("File #");
        latencyGridLabel.setVerticalAxisTitle("One Way Latency (s)");

        latencyGraph.getViewport().setMinX(0);
        latencyGraph.getViewport().setMaxX(15);
        latencyGraph.getViewport().setMinY(0);
        latencyGraph.getViewport().setMaxY(15);
        latencyGraph.getViewport().setYAxisBoundsManual(true);
        latencyGraph.getViewport().setXAxisBoundsManual(true);

        final GraphView throughputGraph = (GraphView) view.findViewById(R.id.throughput_graph);
        throughputGraph.getViewport().setXAxisBoundsManual(true);
        throughputGraph.getViewport().setScrollable(true);

        throughputGraph.removeAllSeries();
        seriesThroughput = new LineGraphSeries<DataPoint>();
        throughputGraph.addSeries(seriesThroughput);
        throughputGraph.setTitle("Throughput");

        GridLabelRenderer throughputGridLabel = throughputGraph.getGridLabelRenderer();
        throughputGridLabel.setHorizontalAxisTitle("File #");
        throughputGridLabel.setVerticalAxisTitle("Throughput (MBs/s)");

        throughputGraph.getViewport().setMinX(0);
        throughputGraph.getViewport().setMaxX(15);
        throughputGraph.getViewport().setMinY(0);
        throughputGraph.getViewport().setMaxY(5);
        throughputGraph.getViewport().setYAxisBoundsManual(true);
        throughputGraph.getViewport().setXAxisBoundsManual(true);

        button1 = view.findViewById(R.id.Submittcp);
        button1.setOnClickListener(new View.OnClickListener() {

            /* supposed to execute the file transfer but it doesn't?*/

            @Override
            public void onClick(View view) {

                latencyGraph.setVisibility(View.VISIBLE);
                throughputGraph.setVisibility(View.VISIBLE);
                final String realip = ip.getText().toString();
                final long timee;
                new Thread(new Runnable() {
                    File sending;
                    double sresult = 0;
                    double throughput = 0;
                    String latencyString = "";
                    String throughputString = "";

                    @Override
                    public void run() {
                        byte[] fileInBytes = new byte[100];
                        int count;


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
                            double sr = result;
                            sresult = (sr / 1000000000);
                            Log.i("TIME TAKEN", Long.toString(result) + "s");
                            throughput = 1.24 / sresult;
                            output.close();
                            input.close();
                            client.close();
                            Log.e("CLIENT", "FINISHED SUCCESSFULLY");

                            latencyString = Double.toString(sresult);
                            throughputString = Double.toString(throughput);

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
                                    oneWaythroughput.setText("Throughput: " + throughputString + "Mbs");
                                    oneWaythroughput.setVisibility(View.VISIBLE);
                                }
                            });

                            latencyGraph.post(new Runnable() {
                                @Override
                                public void run() {
                                    addLatencyEntry(sresult);
                                }
                            });
                            throughputGraph.post(new Runnable() {
                                @Override
                                public void run() {
                                    addThroughputEntry(throughput);
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
                        try {
                            int PORT = 8080;

                            // create socket
                            DatagramSocket client = new DatagramSocket();

                            // empty packet
                            byte[] buf = new byte[100];

                            DatagramPacket packet;
                            AssetManager asst = getActivity().getAssets();
                            InputStream input = asst.open("test.jpg");
                            while (input.read(buf) != -1) {
                                packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(realip), PORT);
                                client.send(packet);
                                Thread.sleep(1);
                            }
                            input.close();

                            Log.d("UDP Client", "SENT FILE");
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


    private void addThroughputEntry(double entry) {
        seriesThroughput.appendData(new DataPoint(lastXThoughput++, entry), false, 15);
    }

    private void addLatencyEntry(double entry) {
        seriesLatency.appendData(new DataPoint(lastXLatency++, entry), false, 15);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
//                TextView s = getView().findViewById(R.id.Filname);
//                s.setText(Data.getData().getPath());
//                file = Data.getData().toString();
            }
        }
    }

    @Override
    public void publish_results(String test) {

    }
}