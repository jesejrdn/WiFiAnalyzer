package com.vrem.wifianalyzer.tcptransfer.server;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.R;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


public class tcpSeverAdapter extends AsyncTask<Void,String,String> {
    private static final int socketServerPORT = 8080;
    String message="";
    ProgressBar prog;
    tcpinterface tcp;
    public tcpSeverAdapter(tcpinterface tcp){
        this.tcp=tcp;
    }
    @Override
    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(Void... Params) {

        String yes="test";
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

                tcpSeverAdapter.SocketServerReplyThread socketServerReplyThread = new tcpSeverAdapter.SocketServerReplyThread(
                        socket, count);
                socketServerReplyThread.run();
                publishProgress(message);

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return yes;
    }
    @Override
    protected void onProgressUpdate(String... Params){
        tcp.publish_results(Params[0]);
    }
    @Override
    protected void onPostExecute(String Params){
        tcp.publish_results(Params);
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
}
