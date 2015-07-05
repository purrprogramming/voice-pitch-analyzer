package lilithwittmann.de.voicepitchanalyzer.models.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import lilithwittmann.de.voicepitchanalyzer.models.Recording;

/**
 * Created by Lilith on 05/07/15.
 */
public class RecordingDB {
    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecordingEntry.TABLE_NAME + " (" +
                    RecordingEntry._ID + " INTEGER PRIMARY KEY," +
                    RecordingEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    RecordingEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    RecordingEntry.COLUMN_NAME_AVG_PITCH + DOUBLE_TYPE + COMMA_SEP +
                    RecordingEntry.COLUMN_NAME_MAX_PITCH + DOUBLE_TYPE + COMMA_SEP +
                    RecordingEntry.COLUMN_NAME_MIN_PITCH + DOUBLE_TYPE + COMMA_SEP +

                    " );"
                    +
                    "CREATE TABLE " + PitchEntry.TABLE_NAME + " (" +
                    PitchEntry._ID + " INTEGER PRIMARY KEY," +
                    PitchEntry.COLUMN_NAME_PITCH + DOUBLE_TYPE + COMMA_SEP +
                    PitchEntry.COLUMN_NAME_OFFSET + DOUBLE_TYPE + COMMA_SEP +
                    " );";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RecordingEntry.TABLE_NAME + ";" +
                    "DROP TABLE IF EXISTS " + PitchEntry.TABLE_NAME + ";";

    public static abstract class RecordingEntry implements BaseColumns {
        public static final String TABLE_NAME = "recording";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_AVG_PITCH = "avg_pitch";
        public static final String COLUMN_NAME_MIN_PITCH = "min_pitch";
        public static final String COLUMN_NAME_MAX_PITCH = "max_pitch";
    }

    public static abstract class PitchEntry implements BaseColumns {
        public static final String TABLE_NAME = "pitch";
        public static final String COLUMN_NAME_PITCH = "pitch";
        public static final String COLUMN_NAME_OFFSET = "offset";
    }

    public class RecordingDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Recording.db";

        public RecordingDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }


}
