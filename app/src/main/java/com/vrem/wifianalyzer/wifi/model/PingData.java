package com.vrem.wifianalyzer.wifi.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import static java.lang.String.*;

public class PingData {

    private Runtime runtime;
    private Thread pingThread;
    private OnPingListener onPingListener;

    public PingData() {
        runtime = Runtime.getRuntime();
    }

    public void setOnPingListener(OnPingListener onPingListener) {
        this.onPingListener = onPingListener;
    }

    public boolean ping(String destination, int timeoutInSeconds) throws InterruptedException {
        String inputLine;
        double avgRtt = 0;
        try {
            String command = format(Locale.US, "/system/bin/ping -c 3 -W %d %s", timeoutInSeconds, destination);
            Process process = runtime.exec(command);
            int ret = process.waitFor();
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder total = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    Log.d("1:STR VALUE", line);
                    total.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("Process", total.toString());
            process.destroy();
            return ret == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void pingUntilSucceeded(final String destination, final long intervalInMillis) {
        pingThread = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    while (true) {
                        if (ping(destination, 3)) {
                            if (onPingListener != null)
                                onPingListener.onPingSuccess();
                            break;
                        } else {
                            if (onPingListener != null)
                                onPingListener.onPingFailure();
                        }

                        Thread.sleep(intervalInMillis);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (onPingListener != null)
                    onPingListener.onPingFinish();
            }
        };
        pingThread.start();
    }

    public void pingUntilFailed(final String destination, final long intervalInMillis) {
        pingThread = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    while (true) {
                        if (!ping(destination, 3)) {
                            if (onPingListener != null)
                                onPingListener.onPingFailure();
                            break;
                        } else {
                            if (onPingListener != null)
                                onPingListener.onPingSuccess();
                        }

                        Thread.sleep(intervalInMillis);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (onPingListener != null)
                    onPingListener.onPingFinish();
            }
        };
        pingThread.start();
    }

    public void cancel() {
        if (pingThread != null)
            pingThread.interrupt();
    }

    public interface OnPingListener {
        void onPingSuccess();
        void onPingFailure();
        void onPingFinish();
    }

}
