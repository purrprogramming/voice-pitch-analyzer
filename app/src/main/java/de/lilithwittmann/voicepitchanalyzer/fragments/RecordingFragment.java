package de.lilithwittmann.voicepitchanalyzer.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
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
import de.lilithwittmann.voicepitchanalyzer.models.database.RecordingDB;
import de.lilithwittmann.voicepitchanalyzer.utils.CountingWriterProcessor;
import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;
import de.lilithwittmann.voicepitchanalyzer.utils.RecordingPaths;
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
    private CountingWriterProcessor writer;
    private int sampleRate;
    private int bufferRate = 4096;
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
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.requestRecordingPermission();

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (isRecording && stopRecording()) {
                    ((Button) view.findViewById(R.id.record_button)).setText(getResources().getString(R.string.start_recording));

                    calculator.getPitches().clear();
                    Optional<Path> recordingPath = Optional.ofNullable(recordingFile)
                            .map(file -> RecordingPaths.getUnfinishedRecordingPath(getContext(), file));
                    // assumption: recordingFile is non-null iff we are presently recording to disk
                    if (recordingPath.isPresent()) {
                        try
                            {
                                Files.delete(recordingPath.get());
                            } catch (IOException ex)
                            {
                                Log.i(LOG_TAG, "error deleting unfinished recording file " + recordingPath.get() + ": " + ex);
                            }
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

                        Optional<Path> unfinishedRecordingPath = Optional.ofNullable(recordingFile)
                                .map(file -> RecordingPaths.getUnfinishedRecordingPath(getContext(), file));
                        Optional<Path> recordingPath = Optional.ofNullable(recordingFile)
                                .map(file -> RecordingPaths.getRecordingPath(getContext(), file));
                        if (unfinishedRecordingPath.isPresent() && recordingPath.isPresent())
                        {
                            try
                            {
                                Files.createDirectories(recordingPath.get().getParent());
                                Files.move(unfinishedRecordingPath.get(), recordingPath.get());
                            } catch (IOException ex)
                            {
                                Log.w(LOG_TAG, "error moving unfinished recording " + unfinishedRecordingPath.get() + " to " +
                                      recordingPath.get() + ": " + ex);
                                recordingFile = null;
                            }
                        }
                        else if (recordingFile != null)
                        {
                            Log.w(LOG_TAG, "could not determine recording paths to move unfinished recording");
                            recordingFile = null;
                        }

                        PitchRange range = new PitchRange();
                        range.setPitches(calculator.getPitches());
                        range.setMin(calculator.calculateMinAverage());
                        range.setMax(calculator.calculateMaxAverage());
                        range.setAvg(calculator.calculatePitchAverage());

                        Recording currentRecord = new Recording(new Date());
                        currentRecord.setRange(range);
                        // assumption: recordingFile and writer are both non-null iff we recorded to disk
                        if (recordingFile != null && writer != null)
                        {
                            currentRecord.setRecording(recordingFile);
                            currentRecord.setRecordingFileSize(writer.getFileSize());
                        }
                        else
                        {
                            Log.w(LOG_TAG, "recording was not written to persistent storage");
                        }

                        RecordingDB recordingDB = new RecordingDB(getActivity());
                        currentRecord = recordingDB.saveRecording(currentRecord);

                        v.setVisibility(View.INVISIBLE);

                        if (mListener != null) {
                            mListener.onRecordFinished(currentRecord.getId());
                        }
                    }
                } else if (recordPitch()) {
                    runTimer((Button) v);
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
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
                }

                else
                {
                    // disable "record" button if recording permission was denied
                    Log.i(LOG_TAG, "permission denied");
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
                        }

                        if (this.dispatcher != null)
                        {
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
                            mListener.onPitchDetected(pitchInHz);

                            Log.i(LOG_TAG, String.format("Pitch: %s", pitchInHz));
                            calculator.addPitch((double) pitchInHz);
                            Log.i(LOG_TAG, String.format("Avg: %s (%s - %s)", calculator.calculatePitchAverage().toString(), calculator.calculateMinAverage().toString(), calculator.calculateMaxAverage().toString()));
                        }
                    });
                }
            };

            AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, this.sampleRate, this.bufferRate, pdh);
            String recordingFile = UUID.randomUUID().toString() + ".wav";
            Path recordingPath = RecordingPaths.getUnfinishedRecordingPath(getActivity(), recordingFile);
            RandomAccessFile openRecordingFile = null;
            if (recordingPath != null)
            {
                try
                {
                    Files.createDirectories(recordingPath.getParent());
                    openRecordingFile = new RandomAccessFile(recordingPath.toFile(), "rw");
                } catch (IOException ex)
                {
                    Log.w(LOG_TAG, "error opening file for recording " + recordingPath + ": " + ex);
                }
            }
            else
            {
                Log.w(LOG_TAG, "could not determine recording path");
            }

            dispatcher.addAudioProcessor(p);

            CountingWriterProcessor writer = null;
            if (openRecordingFile != null)
            {
                writer = new CountingWriterProcessor(dispatcher.getFormat(), openRecordingFile);
                dispatcher.addAudioProcessor(writer);
            }

            this.recordingFile = Optional.ofNullable(writer).map(_f -> recordingFile).get();
            this.writer = writer;
            this.recordThread = new Thread(dispatcher, "Audio Dispatcher");
            this.recordThread.start();

            return this.recordThread.isAlive();
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
        } else {
            this.isRecording = false;
            Log.i(LOG_TAG, "not recording");
            return true;
        }
    }

    private void runTimer(TextView view) {
        long start = System.currentTimeMillis();
        Runnable runnable = () -> view.setText(String.format("%s (%s)",
                getResources().getText(R.string.stop_recording),
                DateUtils.formatElapsedTime((System.currentTimeMillis() - start) / 1000)));
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRecording) {
                    this.cancel();
                    return;
                }
                RecordingFragment.this.getActivity().runOnUiThread(runnable);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 50);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onPitchDetected(float pitchInHz);

        void onRecordFinished(long recordID);

        void onCancel();
    }
}
