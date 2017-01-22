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

        int sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();
        Integer bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, android.media.AudioFormat.CHANNEL_OUT_MONO) * 2;
        track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        this.file = file;
    }


    public void play()
    {
        byte[] byteData = new byte[(int) file.length()];
        Log.d("audioPlayer fileLength", (int) file.length() + "");

        FileInputStream in = null;
        try
        {
            in = new FileInputStream(file);
            in.read(byteData);
            in.close();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        AudioTrack at = this.track;
        if (at != null)
        {
            this.isPlaying = true;
            at.play();
            // Write the byte array to the track
            at.write(byteData, 0, byteData.length);
            at.stop();
            this.isPlaying = false;
            at.release();
        }
        else
        {
            Log.d("audioPlayer", "audio track is not initialised ");
        }

    }

    public void stop()
    {
        if (this.track != null)
        {
            this.track.stop();
            this.isPlaying = false;
        }
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }
}
