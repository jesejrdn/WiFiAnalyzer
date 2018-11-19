
package com.vrem.wifianalyzer.tcptransfer.server;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.tcptransfer.server.tcpServer;

import org.w3c.dom.Text;

public class ServerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recieve_content, container, false);
        Button receive_button = view.findViewById(R.id.tcp_receive);
        Button udp_receive = view.findViewById(R.id.udp_receive);
        final TextView IPaddress=view.findViewById(R.id.ipaddr);

        receive_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                * tcp button is clicked
                 */
                String IP=tcpServer.getIpAddress();
                IPaddress.setText(IP);
                IPaddress.setVisibility(View.VISIBLE);
                /*
                set up send file part
                 */
                tcpServer tcpServe=new tcpServer();
                tcpServe.execute();
                }
        });
            udp_receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    udp click
                     */
                    String IP=tcpServer.getIpAddress();
                    IPaddress.setText(IP);
                    IPaddress.setVisibility(View.VISIBLE);
                    /*
                    set up send for udp
                     */
                }
            });
        return view;
    }
    }
