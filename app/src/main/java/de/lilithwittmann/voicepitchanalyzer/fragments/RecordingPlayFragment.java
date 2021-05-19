package de.lilithwittmann.voicepitchanalyzer.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.activities.RecordViewActivity;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;
import de.lilithwittmann.voicepitchanalyzer.utils.RecordingPaths;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingPlayFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String LOG_TAG = RecordingPlayFragment.class.getSimpleName();
    private SimpleExoPlayer player;

    public RecordingPlayFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @return A new instance of fragment RecordingOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordingPlayFragment newInstance(int sectionNumber)
    {
        RecordingPlayFragment fragment = new RecordingPlayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AudioAttributes playerAttributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_SPEECH)
                .setUsage(C.USAGE_MEDIA)
                .build();

        player = new SimpleExoPlayer.Builder(getContext())
                .setAudioAttributes(playerAttributes, true)
                .build();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        player.stop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        player.release();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording_play, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        player.stop();
        player.clearMediaItems();

        ImageButton playButton = ((ImageButton) view.findViewById(R.id.play_button));

        if (RecordViewActivity.currentRecord != null)
        {
            double average = RecordViewActivity.currentRecord.getRange().getAvg();
            PitchCalculator pitchCalculator = new PitchCalculator();
            pitchCalculator.setPitches(RecordViewActivity.currentRecord.getRange().getPitches());
            ((TextView) view.findViewById(R.id.average)).setText(String.format("%sHz", Math.round(average)));
            ((TextView) view.findViewById(R.id.pitch_min_avg)).setText(String.format("%sHz", Math.round(RecordViewActivity.currentRecord.getRange().getMin())));
            ((TextView) view.findViewById(R.id.pitch_max_avg)).setText(String.format("%sHz", Math.round(RecordViewActivity.currentRecord.getRange().getMax())));

            Double minPitch = pitchCalculator.getMin();
            Double maxPitch = pitchCalculator.getMax();

            ((TextView) view.findViewById(R.id.pitch_min)).setText(String.format("%sHz", minPitch != null ? Math.round(minPitch) : "0"));
            ((TextView) view.findViewById(R.id.pitch_max)).setText(String.format("%sHz", maxPitch != null ? Math.round(maxPitch) : "0"));

            if (average > 0)
            {
                if (average < PitchCalculator.minFemalePitch)
                {
                    ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.male));
                }
                else if (average > PitchCalculator.maxMalePitch)
                {
                    ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.female));
                }
                else
                {
                    ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.in_between));
                }
            }

            else
            {
                ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.unknown));
            }

            MediaItem recordingMediaItem = getRecordingMediaItem();
            if (recordingMediaItem != null)
            {
                long recordingSizeBytes = RecordViewActivity.currentRecord.getRecordingFileSize();
                if (recordingSizeBytes >= 1024 * 1024)
                {
                    double recordingSizeMiB = ((double) recordingSizeBytes) / 1024.0 / 1024.0;
                    ((TextView) view.findViewById(R.id.recording_size)).setText(String.format("%01.2fMiB", recordingSizeMiB));
                }
                else
                {
                    double recordingSizeKiB = ((double) recordingSizeBytes) / 1024.0;
                    ((TextView) view.findViewById(R.id.recording_size)).setText(String.format("%01.2fKiB", recordingSizeKiB));
                }

                playButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (player.isPlaying())
                        {
                            Log.i(LOG_TAG, "stop");
                            player.stop();
                            player.clearMediaItems();
                        }
                        else
                        {
                            Log.i(LOG_TAG, "play");
                            player.setMediaItem(recordingMediaItem);
                            player.prepare();
                            player.play();
                        }
                    }
                });
            }
            else
            {
                ((TextView) view.findViewById(R.id.recording_size)).setText(getString(R.string.none));
                playButton.setVisibility(View.GONE);
            }

        }
        else
        {
            ((TextView) view.findViewById(R.id.recording_size)).setText(getString(R.string.none));
            playButton.setVisibility(View.GONE);
        }
    }

    private MediaItem getRecordingMediaItem()
    {
        Optional<Recording> recording = Optional.ofNullable(RecordViewActivity.currentRecord);
        Optional<String> recordingFilename = recording.map(Recording::getRecording);
        Optional<Path> recordingPath = recordingFilename.map(name -> RecordingPaths.getRecordingPath(getActivity(), name));
        if (!recordingPath.isPresent() || !Files.exists(recordingPath.get()))
        {
            return null;
        }
        return new MediaItem.Builder()
                .setUri(Uri.fromFile(recordingPath.get().toFile()))
                .build();
    }
}
