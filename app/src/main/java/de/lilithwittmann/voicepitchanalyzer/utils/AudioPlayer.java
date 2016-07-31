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
 * Since we process mic input with TarsosDSP which can only does pcm without extra work we have to use
 * AudioTrack instead of MediaPlayer for playback which requires this little player to be written around it.
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

        float length = getTrackLength();
        Log.d("audio player", "track length: " + length + " seconds");

        asyncPlayTrack(audioData, this.track);
    }

    public void stop()
    {
        if (!isTrackInitialized())
            return;

        tryStopPlayer();

        this.isPlaying = false;
    }

    private void initializeTrack() {
        int sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();
        Integer bufferSize = AudioTrack.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.CHANNEL_OUT_MONO) * 2;

        track = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
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

    public void setOnComplete(Handler onComplete) {
        this.onAudioEnd = onComplete;
    }

    private float getTrackLength() {
        int sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();
        return (float) ((audioData.length/sampleRate) / 2.0);
    }
}
