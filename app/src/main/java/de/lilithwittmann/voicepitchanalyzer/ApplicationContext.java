package de.lilithwittmann.voicepitchanalyzer;

import android.app.Application;
import android.util.Log;

import java.util.concurrent.ExecutorService;

import de.lilithwittmann.voicepitchanalyzer.utils.ExecutorUtils;
import de.lilithwittmann.voicepitchanalyzer.utils.RecordingCleaner;

public class ApplicationContext extends Application {
    private static final String LOG_TAG = ApplicationContext.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "onCreate()");

        ExecutorService executor = ExecutorUtils.getBoundedExecutor();
        executor.execute(new RecordingCleaner(this.getApplicationContext()));
    }
}
