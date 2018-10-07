/*
 * WiFiAnalyzer
 * Copyright (C) 2018  VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.vrem.wifianalyzer.wifi.accesspoint;

import android.net.wifi.WifiInfo;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.wifi.model.WiFiConnection;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.scanner.UpdateNotifier;

import java.util.Locale;

public class ConnectionView implements UpdateNotifier {
    static final int COUNT_MAX = 4;
    private static int count = 0;
    private final MainActivity mainActivity;
    private AccessPointDetail accessPointDetail;
    private AccessPointPopup accessPointPopup;

    public ConnectionView(@NonNull MainActivity mainActivity) {
        resetCount();
        this.mainActivity = mainActivity;
        setAccessPointDetail(new AccessPointDetail());
        setAccessPointPopup(new AccessPointPopup());
    }
	//checks if count reaches the COUNT_MAX (4 in this case) don't know what its for yet
    private static boolean isCountMax(boolean noData) {
        if (noData) {
            count++;
        } else {
            resetCount();
        }
        return count >= COUNT_MAX;
    }
	//count is set to 0 if noData is false in previous method
    private static void resetCount() {
        count = 0;
    }

    @Override
    public void update(@NonNull WiFiData wiFiData) {
        ConnectionViewType connectionViewType = MainContext.INSTANCE.getSettings().getConnectionViewType();
        displayConnection(wiFiData, connectionViewType);
        displayScanning(wiFiData);
        displayNoData(wiFiData);
    }
	//setter method
    void setAccessPointDetail(@NonNull AccessPointDetail accessPointDetail) {
        this.accessPointDetail = accessPointDetail;
    }
	//setter method
    void setAccessPointPopup(@NonNull AccessPointPopup accessPointPopup) {
        this.accessPointPopup = accessPointPopup;
    }
	//will display the scanner if there is a wifi connection to be detected
    private void displayScanning(@NonNull WiFiData wiFiData) {
        mainActivity.findViewById(R.id.scanning).setVisibility(noData(wiFiData) ? View.VISIBLE : View.GONE);
    }
	//sets the nodata view visible if there is no wifi data
    private void displayNoData(@NonNull WiFiData wiFiData) {
        mainActivity.findViewById(R.id.nodata).setVisibility(isCountMax(noData(wiFiData)) ? View.VISIBLE : View.GONE);
    }
	//returns whethere there is wifi data to be found
    private boolean noData(@NonNull WiFiData wiFiData) {
        return mainActivity.getCurrentNavigationMenu().isRegistered() && wiFiData.getWiFiDetails().isEmpty();
    }
	//display what the current connection is
    private void displayConnection(@NonNull WiFiData wiFiData, @NonNull ConnectionViewType connectionViewType) {
        WiFiDetail connection = wiFiData.getConnection(); //get wifidata about connection
        View connectionView = mainActivity.findViewById(R.id.connection); //find the view for connection
        WiFiConnection wiFiConnection = connection.getWiFiAdditional().getWiFiConnection(); //get all info about the connection
		/*
		* hides the connection view if view type is hidden, or there is no wifi connection
		*/
        if (connectionViewType.isHide() || !wiFiConnection.isConnected()) {
            connectionView.setVisibility(View.GONE);
        } else {
            connectionView.setVisibility(View.VISIBLE);
            ViewGroup parent = connectionView.findViewById(R.id.connectionDetail);
            View view = accessPointDetail.makeView(parent.getChildAt(0), parent, connection, false, connectionViewType.getAccessPointViewType());
            if (parent.getChildCount() == 0) {
                parent.addView(view);
            }
            setViewConnection(connectionView, wiFiConnection);
            attachPopup(view, connection);
        }
    }
	//get extra info for the connection view
    private void setViewConnection(View connectionView, WiFiConnection wiFiConnection) {
        String ipAddress = wiFiConnection.getIpAddress();
		
		//sets the text of the ipAddress view to the ipaddress
        connectionView.<TextView>findViewById(R.id.ipAddress).setText(ipAddress);
		//grabs the textview called linkspeed
        TextView textLinkSpeed = connectionView.findViewById(R.id.linkSpeed);
        int linkSpeed = wiFiConnection.getLinkSpeed(); //gets the link speed
		/* will then update the textview to be visible or not wether or not the link speed is valid 
		*  and update that link with the information
		*/
        if (linkSpeed == WiFiConnection.LINK_SPEED_INVALID) {
            textLinkSpeed.setVisibility(View.GONE);
        } else {
            textLinkSpeed.setVisibility(View.VISIBLE);
            textLinkSpeed.setText(String.format(Locale.ENGLISH, "%d%s", linkSpeed, WifiInfo.LINK_SPEED_UNITS));
        }
    }
	/*
	* adds the attaches the wifi details to the popupView
	*/
    private void attachPopup(@NonNull View view, @NonNull WiFiDetail wiFiDetail) {
        View popupView = view.findViewById(R.id.attachPopup);
        if (popupView != null) {
            accessPointPopup.attach(popupView, wiFiDetail);
            accessPointPopup.attach(view.findViewById(R.id.ssid), wiFiDetail);
        }
    }

}
