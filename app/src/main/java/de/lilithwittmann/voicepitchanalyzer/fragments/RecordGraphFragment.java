package de.lilithwittmann.voicepitchanalyzer.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.activities.RecordViewActivity;
import de.lilithwittmann.voicepitchanalyzer.utils.GraphValueFormatter;
import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecordGraphFragment extends Fragment implements OnChartValueSelectedListener
{

    private static final String LOG_TAG = RecordGraphFragment.class.getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private OnFragmentInteractionListener mListener;
    private LineDataSet dataSet;
    private LineData lineData;

    public RecordGraphFragment()
    {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecordGraphFragment newInstance(int sectionNumber)
    {
        Bundle bundle = new Bundle();
        //        bundle.putParcelable(Recording.KEY, recording);
        bundle.putInt(ARG_SECTION_NUMBER, sectionNumber);

        RecordGraphFragment recordFragment = new RecordGraphFragment();
        recordFragment.setArguments(bundle);
        //        getSupportFragmentManager().beginTransaction().replace(R.id.container, recordFragment).commit();

        return recordFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_graph, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        LineChart chart = (LineChart) view.findViewById(R.id.recording_chart);
        dataSet = new LineDataSet(mListener.startingPitchEntries(), getResources().getString(R.string.pitch_graph_single_recording));

        dataSet.setCircleColor(getResources().getColor(R.color.indicators));
        dataSet.setColor(getResources().getColor(R.color.indicators));

        lineData = new LineData(ChartData.generateXVals(0, dataSet.getEntryCount()), dataSet);
        chart.setData(lineData);

        long minPitch = Math.round(PitchCalculator.minPitch);
        long maxPitch = Math.round(PitchCalculator.maxPitch);

        // set min/max values etc. for axes
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        chart.getAxisLeft().setStartAtZero(false);
        chart.getAxisRight().setStartAtZero(false);
        chart.getAxisLeft().setAxisMaxValue(maxPitch);
        chart.getAxisRight().setAxisMaxValue(maxPitch);
        chart.getAxisRight().setAxisMinValue(minPitch);
        chart.getAxisLeft().setAxisMinValue(minPitch);

        //        chart.getAxisLeft().setAxisMaxValue(dataSet.getYMax());
        //        chart.getAxisRight().setAxisMaxValue(dataSet.getYMax());
        //        chart.getAxisRight().setAxisMinValue(dataSet.getYMin());
        //        chart.getAxisLeft().setAxisMinValue(dataSet.getYMin());

        // hide grid lines & borders
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setValueFormatter(new GraphValueFormatter());
        chart.getAxisRight().setValueFormatter(new GraphValueFormatter());
        chart.setDrawBorders(false);

        chart.setDescription("");

        // disable all interactions except highlighting selection
        //        chart.setTouchEnabled(false);
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);

        chart.setHighlightEnabled(true);

        chart.setHardwareAccelerationEnabled(true);
//        chart.animateX(3000);
        chart.getLegend().setEnabled(false);
        super.onViewCreated(view, savedInstanceState);
    }

    public void addNewPitch(Entry pitch) {
        if (dataSet == null || getView() == null)
            return;

        lineData.addXValue("" + pitch.getXIndex());
        dataSet.addEntry(pitch);
        lineData.notifyDataChanged();

        LineChart chart = (LineChart) getView().findViewById(R.id.recording_chart);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h)
    {
        e.getVal();
        Log.i(LOG_TAG, String.format("highlight %s selected", h.toString()));
    }

    @Override
    public void onNothingSelected()
    {

    }

    public interface OnFragmentInteractionListener
    {
        List<Entry> startingPitchEntries();
    }
}
