
package com.vrem.wifianalyzer.tcptransfer.client;

import android.content.Intent;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static android.app.Activity.RESULT_OK;

public class ClientFragment extends Fragment implements tcpinterface{
    private static final int PICKFILE_RESULT_CODE = 8778;
    private String file = null;
    TextView finish;
    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_content, container, false);
        EditText ip = view.findViewById(R.id.address);
        final String realip =ip.getText().toString();
        Button button = view.findViewById(R.id.FiletoSend);
        finish=view.findViewById(R.id.finishedtext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte= new Intent(Intent.ACTION_GET_CONTENT);
                inte.setType("*/*");
                inte= Intent.createChooser(inte, "Choose a file");
                startActivityForResult(inte, PICKFILE_RESULT_CODE);

            }
        });
        Button button1 = view.findViewById(R.id.Submittcp);
        button1.setOnClickListener(new View.OnClickListener(){
            /* supposed to execute the file transfer but it doesn't?*/
            @Override
            public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] fileInBytes= new byte[4096];
                            int count;
                            try {
                                /*
                                 * create data inputs for the tcp client to read and write data to other person
                                 */
                                Socket client= new Socket(realip,8080);
                                File sending=new File(file);
                                InputStream input=new FileInputStream(sending);
                                OutputStream output = client.getOutputStream();
                                while((count=input.read(fileInBytes))>0){
                                /*
                                send the data over in chunks
                                 */
                                    output.write(fileInBytes,0,count);
                                    finish.setText("yeet");
                                }
                                output.close();
                                input.close();
                                client.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }).start();

            }
        });
        Button button2 = view.findViewById(R.id.Submitudp);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(file == null){
                    //do nothing here
                } else{
                    // code for UCP client to call
                }
            }});
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
                file=Data.getData().getPath();
            }
        }
    }

    @Override
    public void publish_results(String test) {
        finish.setText(test);
    }
}