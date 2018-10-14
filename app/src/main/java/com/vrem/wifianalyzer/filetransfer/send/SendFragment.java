package com.vrem.wifianalyzer.filetransfer.send;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.vrem.wifianalyzer.R;

public class SendFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_content, container, false);
        TextView sendF = view.findViewById(R.id.Send_File);
        Button button = view.findViewById(R.id.FiletoSend);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // code goes here to select a file
            }
        });
        Button button1 = view.findViewById(R.id.Submit);
        button1.setOnClickListener(new View.OnClickListener(){ @Override
        public void onClick(View view) {
            //code goes here to send the file
        }
        });
        return view;
    }
}