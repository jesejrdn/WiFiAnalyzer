
package com.vrem.wifianalyzer.tcptransfer.client;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vrem.wifianalyzer.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ClientFragment extends Fragment {
    private static final int PICKFILE_RESULT_CODE = 8778;
    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_content, container, false);

        Button button = view.findViewById(R.id.FiletoSend);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte= new Intent(Intent.ACTION_GET_CONTENT);
                inte.setType("*/*");
                inte= Intent.createChooser(inte, "Choose a file");
                startActivityForResult(inte, PICKFILE_RESULT_CODE);
                //sendF.setText(inte.getData().getPath());
            }
        });
        Button button1 = view.findViewById(R.id.Submit);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            //code goes here to send the file
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
                TextView s = getView().findViewById(R.id.FiletoSend);
                s.setText(Data.getData().getPath());
            }
        }
    }
}