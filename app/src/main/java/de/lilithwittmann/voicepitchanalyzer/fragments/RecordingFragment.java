package de.lilithwittmann.voicepitchanalyzer.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import de.lilithwittmann.voicepitchanalyzer.models.database.StorageMaintainer;
import de.lilithwittmann.voicepitchanalyzer.utils.AudioRecorder;
import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;
import de.lilithwittmann.voicepitchanalyzer.utils.SampleRateCalculator;

/**
 * fragment containing text for reading, record/stop and cancel buttons
 */
public class RecordingFragment extends Fragment
{
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
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 235;
    private static final int MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS = 183;

    public RecordingFragment()
    {
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
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.requestRecordingPermission();

        Texts texts = new Texts();
        Context context = getActivity();
        String lang = "en";
        if (texts.supportsLocale(Locale.getDefault().getLanguage()) == Boolean.TRUE)
        {
            lang = Locale.getDefault().getLanguage();
        }

        // get current textNumber
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Integer textNumber = sharedPref.getInt(getString(R.string.saved_text_number), 1);


        // calculate next textNumber
        Integer nextText = 1;
        if (textNumber + 1 < texts.countTexts(lang))
        {
            nextText = textNumber + 1;
        }

        else if (textNumber > texts.countTexts(lang))
        {
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
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isRecording)
                {
                    if (stopRecording())
                    {
                        ((Button) view.findViewById(R.id.record_button)).setText(getResources().getString(R.string.start_recording));

                        calculator.getPitches().clear();
                        // TODO: delete file
                        // new File(recordingFile).delete();
                    }
                }

                mListener.onCancel();
            }
        });

        Button recordButton = (Button) view.findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isRecording)
                {
                    if (stopRecording())
                    {
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

                        StorageMaintainer maintainer = new StorageMaintainer(getActivity());
                        maintainer.cleanupStorage();

                        v.setVisibility(View.INVISIBLE);

                        if (mListener != null)
                        {
                            mListener.onRecordFinished(currentRecord.getId());
                        }
                    }
                }

                else
                {
                    if (recordPitch())
                    {
                        ((Button) v).setText(getResources().getString(R.string.stop_recording));
                    }
                }
            }
        });
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

    private void setSampleRate()
    {
        this.sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();
        Crashlytics.log(Log.DEBUG, "usedSampleRate", String.valueOf(this.sampleRate));
        Log.d("sample rate", String.valueOf(this.sampleRate));
    }

    private void requestRecordingPermission()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            this.requestModifyAudioSettingsPermission();
        }

        else
        {
            //            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
            //                    Manifest.permission.RECORD_AUDIO))
            //            {
            //
            //                // Show an expanation to the user *asynchronously* -- don't block
            //                // this thread waiting for the user's response! After the user
            //                // sees the explanation, try again to request the permission.
            //
            //            }
            //
            //            else
            //            {
            Log.i(LOG_TAG, "request recording permission");

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            //            }
        }
    }

    private void requestModifyAudioSettingsPermission()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.MODIFY_AUDIO_SETTINGS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            this.setSampleRate();
            this.enableRecordButton();
        }

        else
        {
            //            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
            //                    Manifest.permission.MODIFY_AUDIO_SETTINGS))
            //            {
            //
            //                // Show an expanation to the user *asynchronously* -- don't block
            //                // this thread waiting for the user's response! After the user
            //                // sees the explanation, try again to request the permission.
            //
            //            }
            //
            //            else
            //            {
            this.requestPermissions(new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                    MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS);
            //            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        Log.i(LOG_TAG, String.format("permission result in: %s", requestCode));

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // if recording permission was granted, also request permission to modify audio settings
                    this.requestModifyAudioSettingsPermission();
                    Log.i(LOG_TAG, "permission granted");
                    Crashlytics.log(Log.DEBUG, "recordingPermission", "true");
                }

                else
                {
                    // disable "record" button if recording permission was denied
                    Log.i(LOG_TAG, "permission denied");
                    Crashlytics.log(Log.DEBUG, "recordingPermission", "false");
                    this.disableRecordButton();

                    // show explanation why mic permission is needed
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.mic_permission_explanation)
                            .setTitle(R.string.mic_permission_title)
                            .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // get user back to recording overview
                                    // will only affect user after permissions screen is left
                                    mListener.onCancel();

                                    // start app settings activity
                                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:de.lilithwittmann.voicepitchanalyzer")));
                                }

                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // get user back to recording overview
                                    mListener.onCancel();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // if permission for record audio & modify audio settings was granted,
                    // enable "record" button & set sampling rate
                    this.setSampleRate();
                    this.enableRecordButton();
                }

                else
                {
                    // disable "record" button if modify audio settings permission was denied
                    this.disableRecordButton();
                }

                return;
            }
        }
    }

    private void disableRecordButton()
    {
        Log.i(LOG_TAG, "disable record button");
        Button recordButton = (Button) this.getActivity().findViewById(R.id.record_button);
        recordButton.setEnabled(false);
    }

    private void enableRecordButton()
    {
        Button recordButton = (Button) this.getView().findViewById(R.id.record_button);
        recordButton.setEnabled(true);
    }

    public boolean recordPitch()
    {
        if (!this.isRecording)
        {
            this.isRecording = true;
            while (this.dispatcher == null)
            {
                try
                {
                    this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(this.sampleRate, this.bufferRate, 0);
                } catch (Exception exception)
                {
                    Integer usedSampleRate = 0;
                    ArrayList<Integer> testSampleRates = SampleRateCalculator.getAllSupportedSampleRates();
                    for (Integer testSampleRate : testSampleRates)
                    {
                        try
                        {
                            this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(testSampleRate, this.bufferRate, 0);
                        } catch (Exception exception_)
                        {
                            Crashlytics.log(Log.DEBUG, "samplerate !supported", String.valueOf(testSampleRate));
                        }

                        if (this.dispatcher != null)
                        {
                            Crashlytics.log(Log.DEBUG, "support only samplerate", String.valueOf(testSampleRate));
                            break;
                        }
                    }

                }
            }

            if (this.dispatcher == null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.device_sample_rate_not_supported)
                        .setTitle(R.string.device_sample_rate_not_supported_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            PitchDetectionHandler pdh = new PitchDetectionHandler()
            {
                @Override
                public void handlePitch(PitchDetectionResult result, AudioEvent e)
                {
                    final float pitchInHz = result.getPitch();
                    //                    result.
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
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
            try
            {
                fos = getActivity().openFileOutput(this.recordingFile, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            this.recorder = new AudioRecorder(this.sampleRate, this.bufferRate, fos);
            dispatcher.addAudioProcessor(p);
            dispatcher.addAudioProcessor(recorder);
            this.recordThread = new Thread(dispatcher, "Audio Dispatcher");
            this.recordThread.start();

            if (this.recordThread.isAlive())
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        return false;
    }


    private boolean stopRecording()
    {
        if (this.isRecording)
        {
            try
            {
                if (this.dispatcher != null)
                {
                    this.dispatcher.stop();
                }

                if (this.recordThread != null)
                {
                    this.recordThread.stop();
                }
            } catch (Exception ex)
            {
                if (ex.getMessage() != null)
                {
                    Log.i(LOG_TAG, ex.getMessage());
                }
                Log.i(LOG_TAG, ex.getStackTrace().toString());
            }
        }

        if (this.recordThread.isAlive())
        {
            Log.i(LOG_TAG, "still recording");
            return false;
        }
        else
        {
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
    public interface OnFragmentInteractionListener
    {
        public void onRecordFinished(long recordID);

        public void onCancel();
    }
}
