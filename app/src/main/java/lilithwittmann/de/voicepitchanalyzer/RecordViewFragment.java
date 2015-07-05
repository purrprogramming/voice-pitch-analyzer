package lilithwittmann.de.voicepitchanalyzer;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lilithwittmann.de.voicepitchanalyzer.models.Recording;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecordViewFragment extends Fragment {

    private static final String LOG_TAG = RecordViewFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;

    public RecordViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Recording recording = (Recording) savedInstanceState.getSerializable(Recording.KEY);
            Log.i(LOG_TAG, String.format("%s", recording.getDate()));
            Log.i(LOG_TAG, String.format("Avg: %sHz", recording.getRange().getAvg()));
            Log.i(LOG_TAG, String.format("Min: %sHz", recording.getRange().getMin()));
            Log.i(LOG_TAG, String.format("Max: %sHz", recording.getRange().getMax()));
        } else if (this.getArguments() != null) {
            Recording recording = (Recording) this.getArguments().getParcelable(Recording.KEY);
            Log.i(LOG_TAG, String.format("%s", recording.getDate()));
            Log.i(LOG_TAG, String.format("Avg: %sHz", recording.getRange().getAvg()));
            Log.i(LOG_TAG, String.format("Min: %sHz", recording.getRange().getMin()));
            Log.i(LOG_TAG, String.format("Max: %sHz", recording.getRange().getMax()));
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_view, container, false);
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
