package com.vrem.wifianalyzer.wifi.pinggraph;

import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.vrem.wifianalyzer.wifi.graphutils.GraphConstants;

import org.apache.commons.lang3.StringUtils;

public class PingAxisLabel implements LabelFormatter {

    @Override
    public String formatLabel(double value, boolean isValueX) {
        String result = StringUtils.EMPTY;
        int valueAsInt = (int) (value + (value < 0 ? -0.5 : 0.5));
        if (isValueX) {
            if (valueAsInt > 0 && valueAsInt % 2 == 0) {
                result += Integer.toString(valueAsInt);
            }
        } else {
            if (valueAsInt <= GraphConstants.MAX_Y && valueAsInt > GraphConstants.MIN_Y) {
                result += Integer.toString(valueAsInt);
            }
        }
        return result;
    }

    @Override
    public void setViewport(Viewport viewport) {
        // Do nothing
    }

}
