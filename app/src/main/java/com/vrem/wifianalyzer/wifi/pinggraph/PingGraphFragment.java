package com.vrem.wifianalyzer.wifi.pinggraph;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.wifi.graphutils.GraphViewAdd;

import org.apache.commons.collections4.IterableUtils;

public class PingGraphFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private PingGraphAdapter pingGraphAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_content, container, false);

        swipeRefreshLayout = view.findViewById(R.id.graphRefresh);
        swipeRefreshLayout.setOnRefreshListener(new PingGraphFragment.ListViewOnRefreshListener());

        pingGraphAdapter = new PingGraphAdapter();
        addGraphViews(swipeRefreshLayout, pingGraphAdapter);

        MainContext.INSTANCE.getScannerService().register(pingGraphAdapter);

        return view;
    }

    private void addGraphViews(View view, PingGraphAdapter timeGraphAdapter) {
        IterableUtils.forEach(timeGraphAdapter.getGraphViews(),
                new GraphViewAdd((ViewGroup) view.findViewById(R.id.graphFlipper)));
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        MainContext.INSTANCE.getScannerService().update();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onDestroy() {
        MainContext.INSTANCE.getScannerService().unregister(pingGraphAdapter);
        super.onDestroy();
    }

    PingGraphAdapter getPingGraphAdapter() {
        return pingGraphAdapter;
    }

    private class ListViewOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refresh();
        }
    }
}
