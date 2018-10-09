package com.vrem.wifianalyzer.wifi.recieve;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vrem.wifianalyzer.R;

public class RecieveFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recieve_content, container, false);
        TextView sendF = view.findViewById(R.id.Send_File);
        Button button = view.findViewById(R.id.FiletoSend);
        return view;
    }
}