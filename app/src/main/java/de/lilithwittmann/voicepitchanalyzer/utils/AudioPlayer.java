package de.lilithwittmann.voicepitchanalyzer.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by lilith on 7/6/15.
 */
public class AudioPlayer
{
    private boolean isPlaying;
    private final File file;
    AudioTrack track = null;

    public AudioPlayer(File file)
    {
        this.file = file;
    }

    public void play()
    {
        initializeTrack();

        final byte[] byteData = new byte[(int) file.length()];
        Log.d("audioPlayer fileLength", (int) file.length() + " " + file.getAbsolutePath());

        FileInputStream in = null;
        try
        {
            in = new FileInputStream(file);
            in.read(byteData);
            in.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (track != null)
        {
            asyncPlayTrack(byteData);
        }
        else
        {
            Log.d("audioPlayer", "audio track is not initialised ");
        }
    }

    private void asyncPlayTrack(final byte[] byteData) {
        new Thread(new Runnable()
        {
            public void run()
            {
                isPlaying = true;
                track.play();

                track.write(byteData, 0, byteData.length);
                if (isTrackInitialized())
                {
                    track.stop();
                    track.release();
                }

                isPlaying = false;
            }
        }).start();
    }

    private void initializeTrack() {
        int sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();
        Integer bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.CHANNEL_OUT_MONO) * 2;

        track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
    }

    public void stop()
    {
        if (!isTrackInitialized())
            return;

        try
        {
            this.track.pause();
            this.track.flush();
            this.track.release();
        } catch (IllegalStateException e)
        {
            e.printStackTrace();
        }

        this.isPlaying = false;
    }

    private boolean isTrackInitialized() {
        return this.track != null && track.getState() != AudioTrack.STATE_UNINITIALIZED &&
                track.getPlayState() != AudioTrack.PLAYSTATE_STOPPED;
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }
}
