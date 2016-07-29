package de.lilithwittmann.voicepitchanalyzer.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.lilithwittmann.voicepitchanalyzer.R;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by lilith on 7/6/15.
 */
public class AudioPlayer
{
    private boolean isPlaying;
    private InputStream audioIn;
    private byte[] audioData;

    private AudioTrack track = null;

    private Handler onAudioEnd = new Handler();

    public AudioPlayer(File file)
    {
        try {
            audioIn = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public AudioPlayer(InputStream audio)
    {
        this.audioIn = audio;
    }

    public void play()
    {
        initializeTrack();
        if (audioData == null)
            audioData = readAudioData();

        asyncPlayTrack(audioData, this.track);
    }

    private byte[] readAudioData() {
        byte[] audioData = new byte[0];

        try
        {
            audioData = IOUtils.toByteArray(audioIn);
        } catch (IOException e)
        {
            e.printStackTrace();
            onAudioEnd.sendEmptyMessage(-1);
        }

        return audioData;
    }

    private void asyncPlayTrack(final byte[] byteData, final AudioTrack track) {
        new Thread(new Runnable()
        {
            public void run()
            {
                isPlaying = true;
                track.play();
                track.write(byteData, 0, byteData.length);

                if (isTrackInitialized())
                    tryStopPlayer();

                isPlaying = false;
                onAudioEnd.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initializeTrack() {
        int sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();
        Integer bufferSize = AudioTrack.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.CHANNEL_OUT_MONO) * 2;

        track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
    }

    public void stop()
    {
        if (!isTrackInitialized())
            return;

        tryStopPlayer();

        this.isPlaying = false;
    }

    private void tryStopPlayer() {
        try
        {
            // pause() appears to be more snappy in audio cutoff than stop()
            this.track.pause();
            this.track.flush();
            this.track.release();
        } catch (IllegalStateException e)
        {
            // Calling from multiple threads exception, doesn't matter, so just eat it in the rare event it occurs.
            // AudioTrack appears fine for multiple thread usage otherwise.
        }
    }

    private boolean isTrackInitialized() {
        return this.track != null && track.getState() != AudioTrack.STATE_UNINITIALIZED &&
                track.getPlayState() != AudioTrack.PLAYSTATE_STOPPED;
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }

    public void setOnAudioEnd(Handler onAudioEnd) {
        this.onAudioEnd = onAudioEnd;
    }
}
