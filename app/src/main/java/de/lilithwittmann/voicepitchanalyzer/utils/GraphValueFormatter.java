package de.lilithwittmann.voicepitchanalyzer.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Yuri on 06/07/15.
 */
public class GraphValueFormatter implements IAxisValueFormatter {
    private DecimalFormat format = new DecimalFormat("###,###,##0.0");

    public GraphValueFormatter() {
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.format("%sHz", format.format(value));
    }
}
