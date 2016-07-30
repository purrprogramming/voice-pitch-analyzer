package de.lilithwittmann.voicepitchanalyzer.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.activities.RecordViewActivity;
import de.lilithwittmann.voicepitchanalyzer.utils.AudioPlayer;
import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;

public class RecordingPlayFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String LOG_TAG = RecordingPlayFragment.class.getSimpleName();
    private AudioPlayer player;

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

            initializePlayButton(view);
        }
    }

    private void initializePlayButton(View view) {
        final ImageButton playButton = ((ImageButton) view.findViewById(R.id.play_button));

        String audioFile = RecordViewActivity.currentRecord.getRecording();
        if (audioFile == null || audioFile.isEmpty())
        {
            playButton.setVisibility(View.GONE);
            return;
        }

        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (player == null)
                {
                    player = createAudioPlayer(v);
                }

                if (player.isPlaying())
                {
                    Log.i(LOG_TAG, "stop");
                    player.stop();
                    playButton.setImageResource(R.drawable.ic_play);
                }

                else
                {
                    Log.i(LOG_TAG, "play");
                    player.play();
                    playButton.setImageResource(R.drawable.ic_pause_circle_black);
                }
            }
        });
    }

    private AudioPlayer createAudioPlayer(final View view) {
        AudioPlayer player = new AudioPlayer(getActivity().getFileStreamPath(RecordViewActivity.currentRecord.getRecording()));

        AudioEndHandler handler = new AudioEndHandler(view);
        player.setOnAudioEnd(handler);

        return player;
    }

    private static class AudioEndHandler extends Handler {
        private final View view;

        AudioEndHandler(View view) {
            this.view = view;
        }

        public void handleMessage(Message msg)
        {
            final ImageButton playButton = ((ImageButton) view.findViewById(R.id.play_button));
            playButton.setImageResource(R.drawable.ic_play);
        }
    }

    public void onStop() {
        super.onStop();
        stopAudio();
    }

    public void onPause() {
        super.onPause();
        stopAudio();
    }

    public void onDestroyView() {
        super.onDestroyView();
        stopAudio();
    }

    private void stopAudio() {
        try
        {
            if (player != null)
                player.stop();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
