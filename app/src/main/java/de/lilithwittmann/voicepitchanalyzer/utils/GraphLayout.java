package de.lilithwittmann.voicepitchanalyzer.utils;

import android.content.Context;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import de.lilithwittmann.voicepitchanalyzer.R;

/**
 * Created by Yuri on 30.01.16 n. Chr.
 */
public class GraphLayout
{
    /**
     * style a basic chart in app style
     *
     * @param chart chart object to be styled
     * @return styled chart object
     */
    public static BarLineChartBase FormatChart(BarLineChartBase chart)
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

        chart.getAxisLeft().setAxisMaxValue(PitchCalculator.maxPitch.floatValue());
        chart.getAxisRight().setAxisMaxValue(PitchCalculator.maxPitch.floatValue());
        chart.getAxisRight().setAxisMinValue(PitchCalculator.minPitch.floatValue());
        chart.getAxisLeft().setAxisMinValue(PitchCalculator.minPitch.floatValue());

        chart.getAxisLeft().setValueFormatter(new GraphValueFormatter());
        chart.getAxisRight().setValueFormatter(new GraphValueFormatter());
        chart.setDrawBorders(false);

        // set min/max values etc. for axes
        chart.getAxisLeft().setStartAtZero(false);
        chart.getAxisRight().setStartAtZero(false);

        chart.setHardwareAccelerationEnabled(true);
        //        chart.getLegend().setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        return chart;
    }

    /**
     * get bar chart data for male/female vocal ranges
     * to display them as bars beneath other chart data
     *
     * @param amount amount of entries needed
     * @return bar data to add to chart
     */
    public static BarDataSet getOverallRange(Context context, int amount)
    {
        BarDataSet set = new BarDataSet(GraphLayout.getRangeEntries(amount), "");
        set.setDrawValues(false);
        set.setColors(new int[]{context.getResources().getColor(R.color.female_range),
                                context.getResources().getColor(R.color.androgynous_range),
                                context.getResources().getColor(R.color.male_range)});
        set.setStackLabels(new String[]{context.getResources().getString(R.string.male_range),
                                        context.getResources().getString(R.string.androgynous_range),
                                        context.getResources().getString(R.string.female_range)});

        //        List<BarDataSet> setList = new ArrayList<BarDataSet>();
        //        setList.add(set);
        //
        //        return setList;

        return set;
    }

    /***
     * @param amount
     * @return
     */
    private static List<BarEntry> getRangeEntries(int amount)
    {
        List<BarEntry> result = new ArrayList<BarEntry>();

        for (int i = 0; i < amount; i++)
        {
            result.add(new BarEntry(new float[]{
                    PitchCalculator.maxFemalePitch.floatValue() - PitchCalculator.maxMalePitch.floatValue(),
                    PitchCalculator.maxMalePitch.floatValue() - PitchCalculator.minFemalePitch.floatValue(),
                    PitchCalculator.minFemalePitch.floatValue()// - PitchCalculator.minPitch.floatValue(),
            }, i));

            //            result.add(new BarEntry(new float[]{25, 50, 100, 200}, i));
        }

        return result;
    }
}
