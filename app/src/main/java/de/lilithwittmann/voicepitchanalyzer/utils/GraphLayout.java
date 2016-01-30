package de.lilithwittmann.voicepitchanalyzer.utils;

import com.github.mikephil.charting.charts.LineChart;

/**
 * Created by Yuri on 30.01.16 n. Chr..
 */
public class GraphLayout
{
    public static LineChart FormatChart(LineChart chart)
    {
        chart.getAxisLeft().setStartAtZero(false);
        chart.getAxisRight().setStartAtZero(false);

        // hide grid lines & borders
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setValueFormatter(new GraphValueFormatter());
        chart.getAxisRight().setValueFormatter(new GraphValueFormatter());
        chart.setDrawBorders(false);

        chart.setDescription("");

        // disable all interactions except highlighting selection
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);

        chart.setHighlightEnabled(true);

        chart.setHardwareAccelerationEnabled(true);
        chart.getLegend().setEnabled(false);

        return chart;
    }
}
