package de.lilithwittmann.voicepitchanalyzer.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

/**
 * Created by lilith on 7/6/15.
 */
public class AudioRecorder implements AudioProcessor {
    private final FileOutputStream file;
    private boolean firstRun = true;

    public AudioRecorder(Integer sampleRate, Integer bufferRate, FileOutputStream file) {
        this.file = file;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        if (audioEvent == null)
            return true;

        Log.d("recording audio", String.valueOf(audioEvent.getTimeStamp()));

        if (this.firstRun) {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            this.firstRun = Boolean.TRUE;
        }

        try {
            this.file.write(audioEvent.getByteBuffer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public void processingFinished() {
        try {
            this.file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
