package de.lilithwittmann.voicepitchanalyzer.utils;

import android.content.Context;

import java.nio.file.Path;
import java.util.Optional;

public class RecordingPaths {
    public static String RECORDING_DIR = "recordings";
    public static String NEW_RECORDING_DIR = "unfinished_recordings";

    public static Path getUnfinishedRecordingsDirectoryPath(Context context) {
        return Optional.ofNullable(context.getFilesDir())
                .map(dir -> dir.toPath().resolve(NEW_RECORDING_DIR))
                .get();
    }

    public static Path getUnfinishedRecordingPath(Context context, String name) {
        return Optional.ofNullable(getUnfinishedRecordingsDirectoryPath(context))
                .map(dir -> dir.resolve(name))
                .get();
    }

    public static Path getRecordingsDirectoryPath(Context context) {
        return Optional.ofNullable(context.getFilesDir())
                .map(dir -> dir.toPath().resolve(RECORDING_DIR))
                .get();
    }

    public static Path getRecordingPath(Context context, String name) {
        return Optional.ofNullable(getRecordingsDirectoryPath(context))
                .map(dir -> dir.resolve(name))
                .get();
    }
}
