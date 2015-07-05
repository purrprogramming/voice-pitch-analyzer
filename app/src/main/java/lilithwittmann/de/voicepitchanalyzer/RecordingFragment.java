package lilithwittmann.de.voicepitchanalyzer;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Date;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import lilithwittmann.de.voicepitchanalyzer.models.PitchRange;
import lilithwittmann.de.voicepitchanalyzer.models.Recording;
import lilithwittmann.de.voicepitchanalyzer.utils.PitchCalculator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecordingFragment extends Fragment {
    private static final String LOG_TAG = RecordingFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private PitchCalculator calculator = new PitchCalculator();
    private boolean isRecording = false;
    private Thread recordThread;
    private AudioDispatcher dispatcher;

    public RecordingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = (Button) view.findViewById(R.id.record_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    if (stopRecording()) {
                        ((Button) v).setText(getResources().getString(R.string.start_recording));

                        PitchRange range = new PitchRange();
                        range.setMin(calculator.calculateMinAverage());
                        range.setMax(calculator.calculateMaxAverage());
                        range.setAvg(calculator.calculatePitchAverage());
                        range.setPitches(calculator.getPitches());

                        Recording currentRecord = new Recording(new Date());
                        currentRecord.setRange(range);

                        if (mListener != null) {
                            mListener.onRecordFinished(currentRecord);
                        }
                    }
                } else {
                    if (recordPitch()) {
                        ((Button) v).setText(getResources().getString(R.string.stop_recording));
                    }
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Recording recording) {
        if (mListener != null) {
            mListener.onRecordFinished(recording);
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

    public boolean recordPitch() {
        if (!this.isRecording) {
            this.isRecording = true;

            this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

            PitchDetectionHandler pdh = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                    final float pitchInHz = result.getPitch();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(LOG_TAG, String.format("Pitch: %s", pitchInHz));
                            calculator.addPitch((double) pitchInHz);
                            Log.i(LOG_TAG, String.format("Avg: %s (%s - %s)", calculator.calculatePitchAverage().toString(), calculator.calculateMinAverage().toString(), calculator.calculateMaxAverage().toString()));
                        }
                    });
                }
            };

            AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
            dispatcher.addAudioProcessor(p);

            this.recordThread = new Thread(dispatcher, "Audio Dispatcher");
            this.recordThread.start();
        }

        if (this.recordThread.isAlive()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean stopRecording() {
        if (this.isRecording) {
            try {
                if (this.dispatcher != null) {
                    this.dispatcher.stop();
                }

                if (this.recordThread != null) {
                    this.recordThread.stop();
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Log.i(LOG_TAG, ex.getMessage());
                }
                Log.i(LOG_TAG, ex.getStackTrace().toString());
            }
        }

        Log.i(LOG_TAG, "thread alive? " + this.recordThread.isAlive());

        if (this.recordThread.isAlive()) {
            return false;
        } else {
            this.isRecording = false;
            return true;
        }
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
        public void onRecordFinished(Recording recording);
    }
}
