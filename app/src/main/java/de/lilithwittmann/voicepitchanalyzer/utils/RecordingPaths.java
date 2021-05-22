package de.lilithwittmann.voicepitchanalyzer.utils;

import android.content.Context;

import java.nio.file.Path;
import java.util.Optional;

public class RecordingPaths {
    private static String RECORDING_DIR = "recordings";
    private static String UNFINISHED_RECORDING_DIR = "unfinished_recordings";

    public static Path getUnfinishedRecordingsDirectoryPath(Context context) {
        return Optional.ofNullable(context.getFilesDir())
                .map(dir -> dir.toPath().resolve(UNFINISHED_RECORDING_DIR))
                .orElse(null);
    }

    public static Path getUnfinishedRecordingPath(Context context, String name) {
        return Optional.ofNullable(getUnfinishedRecordingsDirectoryPath(context))
                .map(dir -> dir.resolve(name))
                .orElse(null);
    }

    public static Path getRecordingsDirectoryPath(Context context) {
        return Optional.ofNullable(context.getFilesDir())
                .map(dir -> dir.toPath().resolve(RECORDING_DIR))
                .orElse(null);
    }

    public static Path getRecordingPath(Context context, String name) {
        return Optional.ofNullable(getRecordingsDirectoryPath(context))
                .map(dir -> dir.resolve(name))
                .orElse(null);
    }
}
