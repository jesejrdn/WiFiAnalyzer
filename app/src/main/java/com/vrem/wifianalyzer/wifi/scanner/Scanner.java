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

package com.vrem.wifianalyzer.wifi.scanner;
/*
* looks like they use androids wifi library to scan for wifi info
*/
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.model.WiFiData;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IterableUtils;

import java.util.ArrayList;
import java.util.List;

class Scanner implements ScannerService {
    private final List<UpdateNotifier> updateNotifiers;
    private final WifiManager wifiManager;
    private final Settings settings;
    private Transformer transformer;
    private WiFiData wiFiData;
    private Cache cache;
    private PeriodicScan periodicScan;
	/*
	* Scanner object 
	*/
    Scanner(@NonNull WifiManager wifiManager, @NonNull Handler handler, @NonNull Settings settings) {
        this.updateNotifiers = new ArrayList<>();
        this.wifiManager = wifiManager;
        this.settings = settings;
        this.wiFiData = WiFiData.EMPTY;
        this.setTransformer(new Transformer());
        this.setCache(new Cache());
        this.periodicScan = new PeriodicScan(this, handler, settings);
    }
	/*
	* update the scanner. methods enable wifi and scan results are called and data collected from those is created into wifi data
	*/
    @Override
    public void update() {
        enableWiFi();
        scanResults();
        wiFiData = transformer.transformToWiFiData(cache.getScanResults(), wiFiInfo(), wifiConfiguration());
        IterableUtils.forEach(updateNotifiers, new UpdateClosure());
    }
	/*
	* getter method
	*/
    @Override
    @NonNull
    public WiFiData getWiFiData() {
        return wiFiData;
    }
	/*
	* registers an update notifier
	*/
    @Override
    public void register(@NonNull UpdateNotifier updateNotifier) {
        updateNotifiers.add(updateNotifier);
    }
	/*
	* removes an update notifier
	*/
    @Override
    public void unregister(@NonNull UpdateNotifier updateNotifier) {
        updateNotifiers.remove(updateNotifier);
    }
	/*
	* pause the scanning
	*/
    @Override
    public void pause() {
        periodicScan.stop();
    }
	/*
	* boolean if it is scanning
	*/
    @Override
    public boolean isRunning() {
        return periodicScan.isRunning();
    }

    @Override
    public void resume() {
        periodicScan.start();
    }
	/*
	* will try to turn the wifi off on exit? 
	*/
    @Override
    public void setWiFiOnExit() {
        if (settings.isWiFiOffOnExit()) {
            try {
                wifiManager.setWifiEnabled(false);
            } catch (Exception e) {
                // critical error: do not die
            }
        }
    }
	/*
	* more getter and setters
	*/
    @NonNull
    PeriodicScan getPeriodicScan() {
        return periodicScan;
    }

    void setPeriodicScan(@NonNull PeriodicScan periodicScan) {
        this.periodicScan = periodicScan;
    }

    void setCache(@NonNull Cache cache) {
        this.cache = cache;
    }

    void setTransformer(@NonNull Transformer transformer) {
        this.transformer = transformer;
    }

    @NonNull
    List<UpdateNotifier> getUpdateNotifiers() {
        return updateNotifiers;
    }
	/*
	* will make sure that wifi is on if it is off
	*/
    private void enableWiFi() {
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } catch (Exception e) {
            // critical error: do not die
        }
    }

    private void scanResults() {
        try {
            if (wifiManager.startScan()) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                cache.add(scanResults);
            }
        } catch (Exception e) {
            // critical error: do not die
        }
    }

    private WifiInfo wiFiInfo() {
        try {
            return wifiManager.getConnectionInfo();
        } catch (Exception e) {
            // critical error: do not die
            return null;
        }
    }

    private List<WifiConfiguration> wifiConfiguration() {
        try {
            return wifiManager.getConfiguredNetworks();
        } catch (Exception e) {
            // critical error: do not die
            return new ArrayList<>();
        }
    }

    private class UpdateClosure implements Closure<UpdateNotifier> {
        @Override
        public void execute(UpdateNotifier updateNotifier) {
            updateNotifier.update(wiFiData);
        }
    }
}
