package lilithwittmann.de.voicepitchanalyzer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import lilithwittmann.de.voicepitchanalyzer.utils.GraphValueFormatter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecordGraphFragment extends Fragment {

    private static final String LOG_TAG = RecordGraphFragment.class.getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private OnFragmentInteractionListener mListener;

    public RecordGraphFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RecordGraphFragment newInstance(int sectionNumber) {
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
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
//            this.currentRecord = (Recording) savedInstanceState.getSerializable(Recording.KEY);
//            Log.i(LOG_TAG, String.format("%s", recording.getDate()));
//            Log.i(LOG_TAG, String.format("Avg: %sHz", recording.getRange().getAvg()));
//            Log.i(LOG_TAG, String.format("Min: %sHz", recording.getRange().getMin()));
//            Log.i(LOG_TAG, String.format("Max: %sHz", recording.getRange().getMax()));
        } else if (this.getArguments() != null) {
//            this.currentRecord = this.getArguments().getParcelable(Recording.KEY);
//            Log.i(LOG_TAG, String.format("%s", recording.getDate()));
//            Log.i(LOG_TAG, String.format("Avg: %sHz", recording.getRange().getAvg()));
//            Log.i(LOG_TAG, String.format("Min: %sHz", recording.getRange().getMin()));
//            Log.i(LOG_TAG, String.format("Max: %sHz", recording.getRange().getMax()));
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_graph, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LineChart chart = (LineChart) view.findViewById(R.id.recording_chart);
        LineDataSet dataSet = new LineDataSet(RecordViewActivity.currentRecord.getRange().getGraphEntries(), getResources().getString(R.string.pitch_graph_single_recording));
        LineData lineData = new LineData(ChartData.generateXVals(0, dataSet.getEntryCount()), dataSet);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        chart.setData(lineData);
        chart.getAxisLeft().setStartAtZero(false);
        chart.getAxisRight().setStartAtZero(false);
        chart.getAxisLeft().setAxisMaxValue(dataSet.getYMax());
        chart.getAxisRight().setAxisMaxValue(dataSet.getYMax());
        chart.getAxisRight().setAxisMinValue(dataSet.getYMin());
        chart.getAxisLeft().setAxisMinValue(dataSet.getYMin());
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setValueFormatter(new GraphValueFormatter());
        chart.getAxisRight().setValueFormatter(new GraphValueFormatter());
        chart.setDescription("");
        chart.setDrawBorders(false);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        chart.animateX(3000);
        chart.getLegend().setEnabled(false);
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
