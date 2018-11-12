package com.vrem.wifianalyzer.wifi.pinggraph;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.vrem.wifianalyzer.Configuration;
import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.graphutils.GraphConstants;
import com.vrem.wifianalyzer.wifi.graphutils.GraphViewBuilder;
import com.vrem.wifianalyzer.wifi.graphutils.GraphViewNotifier;
import com.vrem.wifianalyzer.wifi.graphutils.GraphViewWrapper;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.predicate.FilterPredicate;

import org.apache.commons.collections4.Predicate;

import java.util.List;
import java.util.Set;

public class PingGraphView implements GraphViewNotifier {

    private final WiFiBand wiFiBand;
    private PingDataManager pingDataManager;
    private GraphViewWrapper graphViewWrapper;

    PingGraphView(@NonNull WiFiBand wiFiBand) {
        this.wiFiBand = wiFiBand;
        this.graphViewWrapper = makeGraphViewWrapper();
        this.pingDataManager = new PingDataManager();
    }

    @Override
    public void update(@NonNull WiFiData wiFiData) {
        Settings settings = MainContext.INSTANCE.getSettings();

        //TODO: May just remove these three lines and get a list of ping detailes?
        Predicate<WiFiDetail> predicate = FilterPredicate.makeOtherPredicate(settings);

        // TODO: Must be replaced with getPingDeatails?
        List<WiFiDetail> wiFiDetails = wiFiData.getWiFiDetails(predicate, settings.getSortBy());

        // TODO: This will add new ping series data to the graph
        Set<WiFiDetail> newSeries = pingDataManager.addSeriesData(graphViewWrapper, wiFiDetails, settings.getGraphMaximumY());

        graphViewWrapper.removeSeries(newSeries);
        graphViewWrapper.updateLegend(settings.getTimeGraphLegend());
        graphViewWrapper.setVisibility(isSelected() ? View.VISIBLE : View.GONE);
    }

    private boolean isSelected() {
        return wiFiBand.equals(MainContext.INSTANCE.getSettings().getWiFiBand());
    }

    @Override
    @NonNull
    public GraphView getGraphView() {
        return graphViewWrapper.getGraphView();
    }

    private int getNumX() {
        return GraphConstants.NUM_X_TIME;
    }

    void setGraphViewWrapper(@NonNull GraphViewWrapper graphViewWrapper) {
        this.graphViewWrapper = graphViewWrapper;
    }

    void setDataManager(@NonNull PingDataManager pingDataManager) {
        this.pingDataManager = pingDataManager;
    }

    @NonNull
    private GraphView makeGraphView(@NonNull MainContext mainContext, Settings settings) {
        Resources resources = mainContext.getResources();
        return new GraphViewBuilder(mainContext.getContext(), getNumX(), settings.getGraphMaximumY(), settings.getThemeStyle())
                .setLabelFormatter(new PingAxisLabel())
                .setVerticalTitle(resources.getString(R.string.graph_axis_y))
                .setHorizontalTitle(resources.getString(R.string.graph_time_axis_x))
                .setHorizontalLabelsVisible(false)
                .build();
    }

    @NonNull
    private GraphViewWrapper makeGraphViewWrapper() {
        MainContext mainContext = MainContext.INSTANCE;
        Settings settings = mainContext.getSettings();
        Configuration configuration = mainContext.getConfiguration();
        GraphView graphView = makeGraphView(mainContext, settings);
        graphViewWrapper = new GraphViewWrapper(graphView, settings.getTimeGraphLegend());
        configuration.setSize(graphViewWrapper.getSize(graphViewWrapper.calculateGraphType()));
        graphViewWrapper.setViewport();
        return graphViewWrapper;
    }

}
