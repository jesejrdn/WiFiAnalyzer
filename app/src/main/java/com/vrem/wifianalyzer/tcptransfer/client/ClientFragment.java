
package com.vrem.wifianalyzer.tcptransfer.client;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vrem.wifianalyzer.R;

public class ClientFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recieve_content, container, false);

        return view;

    }
    @Override
    public void onResume() {
        super.onResume();
    }
}