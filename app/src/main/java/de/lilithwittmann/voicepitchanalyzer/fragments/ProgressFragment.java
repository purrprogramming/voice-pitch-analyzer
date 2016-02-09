package de.lilithwittmann.voicepitchanalyzer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.models.RecordingList;
import de.lilithwittmann.voicepitchanalyzer.utils.GraphLayout;
import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;

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
        LineChart chart = (LineChart) view.findViewById(R.id.progress_chart);
        this.recordings = new RecordingList(this.getContext());

        if (this.recordings != null)
        {
            LineDataSet dataSet = new LineDataSet(this.recordings.getGraphEntries(), getResources().getString(R.string.progress));
            LineData lineData = new LineData(this.recordings.getDates(), dataSet);

            dataSet.setDrawCubic(true);
            dataSet.enableDashedLine(10, 10, 0);

            dataSet.setCircleColor(getResources().getColor(R.color.indicators));
            dataSet.setColor(getResources().getColor(R.color.indicators));

            chart.setData(lineData);
            GraphLayout.FormatChart(chart);

            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            chart.getAxisLeft().setAxisMaxValue(PitchCalculator.maxPitch.floatValue());
            chart.getAxisRight().setAxisMaxValue(PitchCalculator.maxPitch.floatValue());
            chart.getAxisRight().setAxisMinValue(PitchCalculator.minPitch.floatValue());
            chart.getAxisLeft().setAxisMinValue(PitchCalculator.minPitch.floatValue());
        }

        super.onViewCreated(view, savedInstanceState);
    }
}
