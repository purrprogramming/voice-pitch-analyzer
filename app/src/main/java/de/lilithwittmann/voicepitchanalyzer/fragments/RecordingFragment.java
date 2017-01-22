package de.lilithwittmann.voicepitchanalyzer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.models.PitchRange;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import de.lilithwittmann.voicepitchanalyzer.models.Texts;
import de.lilithwittmann.voicepitchanalyzer.models.database.RecordingDB;
import de.lilithwittmann.voicepitchanalyzer.utils.AudioRecorder;
import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;
import de.lilithwittmann.voicepitchanalyzer.utils.SampleRateCalculator;

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
    private int sampleRate;
    private int bufferRate = 4096;
    private AudioRecorder recorder;
    private String recordingFile;

    public RecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();
        Crashlytics.log(Log.DEBUG, "usedSampleRate", String.valueOf(this.sampleRate));
        Log.d("sample rate", String.valueOf(this.sampleRate));
        Texts texts = new Texts();
        Context context = getActivity();
        String lang = "en";
        if (texts.supportsLocale(Locale.getDefault().getLanguage()) == Boolean.TRUE) {
            lang = Locale.getDefault().getLanguage();
        }

        // get current textNumber
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Integer textNumber = sharedPref.getInt(getString(R.string.saved_text_number), 1);


        // calculate next textNumber
        Integer nextText = 1;
        if (textNumber + 1 < texts.countTexts(lang)) {
            nextText = textNumber + 1;
        } else if (textNumber > texts.countTexts(lang)) {
            //really special case - if the user changes the device language and in the new language there are
            // less text samples available than in the previous one, set the text number back to one
            textNumber = 1;
            nextText = 2;
        }

        //save next textNumber
        sharedPref.edit().putInt(getString(R.string.saved_text_number), nextText).apply();

        //render text
        final TextView recording_text = (TextView) view.findViewById(R.id.recording_text);
        recording_text.setText(texts.getText(lang, textNumber));

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    if (stopRecording()) {
                        ((Button) view.findViewById(R.id.record_button)).setText(getResources().getString(R.string.start_recording));

                        calculator.getPitches().clear();
                        // TODO: delete file
                        // new File(recordingFile).delete();
                    }
                }

                else {
                    mListener.onCancel();
                }
            }
        });

        Button recordButton = (Button) view.findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    if (stopRecording()) {
                        ((Button) v).setText(getResources().getString(R.string.start_recording));

                        //Log.d("stream state", String.valueOf(recorder.getRecording().getState()));

                        PitchRange range = new PitchRange();
                        range.setPitches(calculator.getPitches());
                        range.setMin(calculator.calculateMinAverage());
                        range.setMax(calculator.calculateMaxAverage());
                        range.setAvg(calculator.calculatePitchAverage());

                        Recording currentRecord = new Recording(new Date());
                        currentRecord.setRange(range);
                        currentRecord.setRecording(recordingFile);

                        RecordingDB recordingDB = new RecordingDB(getActivity());
                        currentRecord = recordingDB.saveRecording(currentRecord);

                        v.setVisibility(View.INVISIBLE);

                        if (mListener != null) {
                            mListener.onRecordFinished(currentRecord.getId());
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
            while (this.dispatcher == null)
            try {
                this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(this.sampleRate, this.bufferRate, 0);
            } catch (Exception exception) {
                Integer usedSampleRate = 0;
                ArrayList<Integer> testSampleRates = SampleRateCalculator.getAllSupportedSampleRates();
                for( Integer testSampleRate: testSampleRates) {
                    try {
                        this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(testSampleRate, this.bufferRate, 0);
                    } catch (Exception exception_) {
                        Crashlytics.log(Log.DEBUG,"samplerate !supported", String.valueOf(testSampleRate));
                    }

                    if(this.dispatcher != null) {
                        Crashlytics.log(Log.DEBUG, "support only samplerate", String.valueOf(testSampleRate));
                        break;
                    }
                }

            }

            if(this.dispatcher == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.device_sample_rate_not_supported)
                        .setTitle(R.string.device_sample_rate_not_supported_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            PitchDetectionHandler pdh = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                    final float pitchInHz = result.getPitch();
                    //                    result.
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

            AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, this.sampleRate, this.bufferRate, pdh);
            FileOutputStream fos = null;
            this.recordingFile = UUID.randomUUID().toString() + ".pcm";
            try {
                fos = getActivity().openFileOutput(this.recordingFile, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //this.recorder = new AudioRecorder(this.sampleRate, this.bufferRate, fos);
            dispatcher.addAudioProcessor(p);
            //dispatcher.addAudioProcessor(recorder);
            this.recordThread = new Thread(dispatcher, "Audio Dispatcher");
            this.recordThread.start();
            if (this.recordThread.isAlive()) {
                return true;
            } else {
                return false;
            }
        }

        return false;
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

        if (this.recordThread.isAlive()) {
            Log.i(LOG_TAG, "still recording");
            return false;
        } else {
            this.isRecording = false;
            Log.i(LOG_TAG, "not recording");
            return true;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        public void onRecordFinished(long recordID);
        public void onCancel();
    }
}
