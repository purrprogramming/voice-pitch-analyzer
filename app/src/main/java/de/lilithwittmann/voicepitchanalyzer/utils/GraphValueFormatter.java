package de.lilithwittmann.voicepitchanalyzer.utils;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Yuri on 06/07/15.
 */
public class GraphValueFormatter implements YAxisValueFormatter {
    private DecimalFormat format = new DecimalFormat("###,###,##0.0");

    public GraphValueFormatter() {
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return String.format("%sHz", format.format(value));
    }
}
