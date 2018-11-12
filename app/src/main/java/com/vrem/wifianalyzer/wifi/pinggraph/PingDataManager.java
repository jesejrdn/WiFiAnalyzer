package com.vrem.wifianalyzer.wifi.pinggraph;

import android.support.annotation.NonNull;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.vrem.wifianalyzer.wifi.graphutils.GraphConstants;
import com.vrem.wifianalyzer.wifi.graphutils.GraphViewWrapper;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IterableUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PingDataManager {
    private int scanCount;
    private int xValue;
    private PingGraphCache pingGraphCache;

    PingDataManager() {
        this.scanCount = 0;
        this.xValue = 0;
        this.pingGraphCache = new PingGraphCache();
    }

    @NonNull
    Set<WiFiDetail> addSeriesData(@NonNull GraphViewWrapper graphViewWrapper, @NonNull List<WiFiDetail> wiFiDetails, int levelMax) {
        Set<WiFiDetail> inOrder = new TreeSet<>(wiFiDetails);
        IterableUtils.forEach(inOrder, new PingDataManager.AddDataClosure(graphViewWrapper, levelMax));
        adjustData(graphViewWrapper, inOrder);
        Set<WiFiDetail> result = getNewSeries(inOrder);
        xValue++;
        if (scanCount < GraphConstants.MAX_SCAN_COUNT) {
            scanCount++;
        }
        if (scanCount == 2) {
            graphViewWrapper.setHorizontalLabelsVisible(true);
        }
        return result;
    }

    void adjustData(@NonNull GraphViewWrapper graphViewWrapper, @NonNull Set<WiFiDetail> wiFiDetails) {
        IterableUtils.forEach(graphViewWrapper.differenceSeries(wiFiDetails), new PingDataManager.AdjustDataClosure(graphViewWrapper));
        pingGraphCache.clear();
    }

    @NonNull
    Set<WiFiDetail> getNewSeries(@NonNull Set<WiFiDetail> wiFiDetails) {
        Set<WiFiDetail> results = new HashSet<>(wiFiDetails);
        results.addAll(pingGraphCache.active());
        return results;
    }

    void addData(@NonNull GraphViewWrapper graphViewWrapper, @NonNull WiFiDetail wiFiDetail, int levelMax) {
        boolean drawBackground = wiFiDetail.getWiFiAdditional().getWiFiConnection().isConnected();
        int level = Math.min(wiFiDetail.getWiFiSignal().getLevel(), levelMax);
        if (graphViewWrapper.isNewSeries(wiFiDetail)) {
            DataPoint dataPoint = new DataPoint(xValue, scanCount > 0
                    ? GraphConstants.MIN_Y + GraphConstants.MIN_Y_OFFSET : level);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{dataPoint});
            graphViewWrapper.addSeries(wiFiDetail, series, drawBackground);
        } else {
            DataPoint dataPoint = new DataPoint(xValue, level);
            graphViewWrapper.appendToSeries(wiFiDetail, dataPoint, scanCount, drawBackground);
        }
        pingGraphCache.reset(wiFiDetail);
    }

    void setTimeGraphCache(@NonNull PingGraphCache pingGraphCache) {
        this.pingGraphCache = pingGraphCache;
    }

    int getXValue() {
        return xValue;
    }

    int getScanCount() {
        return scanCount;
    }

    void setScanCount(int scanCount) {
        this.scanCount = scanCount;
    }

    private class AddDataClosure implements Closure<WiFiDetail> {
        private final GraphViewWrapper graphViewWrapper;
        private final int levelMax;

        private AddDataClosure(GraphViewWrapper graphViewWrapper, int levelMax) {
            this.graphViewWrapper = graphViewWrapper;
            this.levelMax = levelMax;
        }

        @Override
        public void execute(WiFiDetail wiFiDetail) {
            addData(graphViewWrapper, wiFiDetail, levelMax);
        }
    }

    private class AdjustDataClosure implements Closure<WiFiDetail> {
        private final GraphViewWrapper graphViewWrapper;

        private AdjustDataClosure(GraphViewWrapper graphViewWrapper) {
            this.graphViewWrapper = graphViewWrapper;
        }

        @Override
        public void execute(WiFiDetail wiFiDetail) {
            DataPoint dataPoint = new DataPoint(xValue, GraphConstants.MIN_Y + GraphConstants.MIN_Y_OFFSET);
            boolean drawBackground = wiFiDetail.getWiFiAdditional().getWiFiConnection().isConnected();
            graphViewWrapper.appendToSeries(wiFiDetail, dataPoint, scanCount, drawBackground);
            pingGraphCache.add(wiFiDetail);
        }
    }
}
