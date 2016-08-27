package de.lilithwittmann.voicepitchanalyzer.models.database;


import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;

import java.io.File;
import java.util.List;

import static de.lilithwittmann.voicepitchanalyzer.models.database.RecordingDB.RecordingEntry.*;

/**
 * Saving many recording files to disk every day can chew up phone storage space so here we make
 * sure storage used does not exceed a certain threshold.
 */
public class StorageMaintainer {
    private static final int SAVED_RECORDINGS_LIMIT = 100;
    private final Context context;

    public StorageMaintainer(Context context) {
        this.context = context;
    }

    public void cleanupStorage() {
        try
        {
            removeOldRecordingFiles();
        } catch (Exception ex) {
            // Don't want to crash the app on a non completely necessary maintenance task so ignore exceptions.
            ex.printStackTrace();
            Crashlytics.log(Log.DEBUG, "storage limiter", "Error removing old recording files: " + ex.getMessage());
        }
    }

    private void removeOldRecordingFiles() {
        RecordingDB db = new RecordingDB(context);

        List<Recording> recordings = db.getRecordings(COLUMN_NAME_FILE + " IS NOT NULL");
        Log.d("storage maintainer", "Found " + recordings.size() + " recordings with files.");

        if (recordings.size() < SAVED_RECORDINGS_LIMIT)
            return;

        List<Recording> toDelete = recordings.subList(SAVED_RECORDINGS_LIMIT, recordings.size());
        int deletedCount = 0;

        for (Recording recordOn : toDelete)
            deletedCount += removeRecordingFile(db, recordOn);

        Log.d("storage maintainer", "Deleted " + deletedCount + " recording files.");
    }

    private int removeRecordingFile(RecordingDB db,  Recording toDelete) {
        File file = context.getFileStreamPath(toDelete.getRecording());
        int status = 0;

        if (file != null && file.exists())
        {
            if (file.delete())
                status = 1;
        }

        db.updateRecordingFile(toDelete.getId(), null);

        return status;
    }
}
