package de.lilithwittmann.voicepitchanalyzer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.models.RecordingList;
import de.lilithwittmann.voicepitchanalyzer.utils.GraphLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragment extends Fragment
{
    private RecordingList recordings;

    public ProgressFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        CombinedChart chart = (CombinedChart) view.findViewById(R.id.progress_chart);
        this.recordings = new RecordingList(this.getContext());

        if (this.recordings != null)
        {
            List<String> dates = this.recordings.getDates();

            CombinedData data = new CombinedData(dates);

            LineDataSet dataSet = new LineDataSet(this.recordings.getGraphEntries(), getResources().getString(R.string.progress));
            LineData lineData = new LineData(dates, dataSet);
            BarData barData = new BarData(dates, GraphLayout.getOverallRange(this.getContext(), dates.size()));

            dataSet.setDrawCubic(true);
            dataSet.enableDashedLine(10, 10, 0);
            dataSet.setLineWidth(3f);
            dataSet.setDrawValues(false);

            dataSet.setCircleColor(getResources().getColor(R.color.canvas_dark));
            dataSet.setColor(getResources().getColor(R.color.canvas_dark));
            dataSet.setCircleSize(5f);

            dataSet.setCubicIntensity(0.05f);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            data.setData(lineData);
            data.setData(barData);
            chart.setData(data);
            GraphLayout.FormatChart(chart);

            chart.setTouchEnabled(true);
//            chart.setScaleEnabled(true);
            chart.setPinchZoom(true);
//            chart.setDoubleTapToZoomEnabled(true);

            chart.setDrawValueAboveBar(false);
            chart.setDrawOrder(new DrawOrder[]{
                    DrawOrder.BAR,
                    DrawOrder.BUBBLE,
                    DrawOrder.CANDLE,
                    DrawOrder.LINE,
                    DrawOrder.SCATTER
            });
        }

        super.onViewCreated(view, savedInstanceState);
    }
}
