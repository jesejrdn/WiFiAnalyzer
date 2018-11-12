package com.vrem.wifianalyzer.wifi.pinggraph;

import android.support.annotation.NonNull;

import com.vrem.util.EnumUtils;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.graphutils.GraphAdapter;
import com.vrem.wifianalyzer.wifi.graphutils.GraphViewNotifier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.List;

public class PingGraphAdapter extends GraphAdapter {

    PingGraphAdapter() {
        super(makeGraphViewNotifiers());
    }

    @NonNull
    private static List<GraphViewNotifier> makeGraphViewNotifiers() {
        return new ArrayList<>(CollectionUtils.collect(EnumUtils.values(WiFiBand.class), new PingGraphAdapter.ToGraphViewNotifier()));
    }

    private static class ToGraphViewNotifier implements Transformer<WiFiBand, GraphViewNotifier> {
        @Override
        public GraphViewNotifier transform(WiFiBand input) {
            return new PingGraphView(input);
        }
    }
}
