package de.lilithwittmann.voicepitchanalyzer.utils;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Yuri on 06/07/15.
 */
public class GraphValueFormatter implements ValueFormatter {
    private DecimalFormat format = new DecimalFormat("###,###,##0.0");

    public GraphValueFormatter() {
    }

    @Override
    public String getFormattedValue(float value) {
        return String.format("%sHz", format.format(value));
    }
}
