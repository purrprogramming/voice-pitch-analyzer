package de.lilithwittmann.voicepitchanalyzer.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import de.lilithwittmann.voicepitchanalyzer.models.database.RecordingDB;

public class RecordingCleaner implements Runnable {
    private static final String LOG_TAG = RecordingCleaner.class.getSimpleName();

    private final Context context;

    private static final long MAX_RECORDINGS_SIZE = 1024 * 1024 * 100;
    private static final int MIN_RECORDING_DAYS = 1;

    public RecordingCleaner(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        cleanUnfinishedRecordings(this.context);
        cleanRecordings(this.context);
    }

    public static void cleanUnfinishedRecordings(Context context) {
        Path directory = RecordingPaths.getUnfinishedRecordingsDirectoryPath(context);
        if (directory == null || !Files.isDirectory(directory)) {
            return;
        }
        DirectoryStream<Path> directoryStream;
        try {
            directoryStream = Files.newDirectoryStream(directory);
        } catch (IOException ex) {
            Log.w(LOG_TAG, "error opening recording directory " + directory + ": " + ex);
            return;
        }

        long deleteCount = 0;
        for (Path filePath : directoryStream) {
            try {
                Files.deleteIfExists(filePath);
                deleteCount += 1;
            } catch (IOException ex) {
                Log.w(LOG_TAG, "error deleting unfinished recording " + filePath + ": " + ex);
            }
        }

        Log.i(LOG_TAG, "deleted " + deleteCount + " unfinished recording files");
    }

    public static void cleanRecordings(Context context) {
        HashSet<String> remainingFiles;
        try {
            remainingFiles = getRecordingFiles(context);
        } catch (IOException ex) {
            Log.w(LOG_TAG, "error listing recordings directory: " + ex);
            remainingFiles = null;
        }

        RecordingDB db = new RecordingDB(context);

        Date newestDeleteDate = Date.from(Instant.now().minus(Duration.ofDays(MIN_RECORDING_DAYS)));
        long runningSize = 0;
        long deletedSize = 0;
        long runningCount = 0;
        long deleteCount = 0;
        long oldCount = 0;
        for (Recording recording : db.getRecordingsWithFiles()) {
            runningCount += 1;
            runningSize += recording.getRecordingFileSize();
            if (remainingFiles != null && recording.getRecording() != null) {
                remainingFiles.remove(recording.getRecording());
            }
            if (recording.getDate().before(newestDeleteDate)) {
                oldCount += 1;
                if (runningSize > MAX_RECORDINGS_SIZE) {
                    DeleteResult deleteResult = deleteRecording(context, db, recording);
                    if (deleteResult == DeleteResult.OK || deleteResult == DeleteResult.MISSING) {
                        deleteCount += 1;
                    }
                    if (deleteResult == DeleteResult.OK) {
                        deletedSize += recording.getRecordingFileSize();
                    }
                }
            }
        }

        Log.i(LOG_TAG, "deleted " + deleteCount + " of " + oldCount + " old recording files (of " + runningCount + " total) freeing " + deletedSize
              + " of " + runningSize + " bytes");

        long deletedOrphanCount = 0;
        for (String file : Optional.ofNullable(remainingFiles).orElseGet(HashSet::new)) {
            Path path = RecordingPaths.getRecordingPath(context, file);
            if (path != null) {
                try {
                    Files.delete(path);
                    deletedOrphanCount += 1;
                } catch (IOException ex) {
                    Log.w(LOG_TAG, "failed to delete orphaned recording file " + path + ": " + ex);
                }
            } else {
                Log.w(LOG_TAG, "could not determine path for orphaned recording file " + file);
            }
        }

        Log.i(LOG_TAG, "deleted " + deletedOrphanCount + " orphaned recording files");
    }

    private static HashSet<String> getRecordingFiles(Context context) throws IOException {
        Path path = RecordingPaths.getRecordingsDirectoryPath(context);
        if (path == null) {
            Log.w(LOG_TAG, "could not determine recordings path");
            return null;
        }
        return Files.list(path)
                .map(filePath -> filePath.getFileName().toString())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static DeleteResult deleteRecording(Context context, RecordingDB db, Recording recording) {
        Optional<Path> path = Optional.ofNullable(recording.getRecording())
                .map(name -> RecordingPaths.getRecordingPath(context, name));
        if (!path.isPresent()) {
            Log.w(LOG_TAG, "could not determine path for recording file " + recording.getRecording());
            return DeleteResult.ERROR;
        }
        DeleteResult result;
        try {
            Files.delete(path.get());
            result = DeleteResult.OK;
        } catch (NoSuchFileException ex) {
            Log.w(LOG_TAG, "recording file " + path.get() + " missing");
            result = DeleteResult.MISSING;
        } catch (IOException ex) {
            Log.w(LOG_TAG, "error deleting recording file " + path.get() + ": ", ex);
            return DeleteResult.ERROR;
        }
        db.updateRecordingFilename(recording.getId(), null);
        return result;
    }

    private enum DeleteResult {
        OK,
        ERROR,
        MISSING,
    }
}
