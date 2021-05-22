package de.lilithwittmann.voicepitchanalyzer.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import com.github.mikephil.charting.data.Entry;

import java.util.Date;

/**
 * Created by Yuri on 04/07/15.
 */
public class Recording implements Parcelable {
    public static final String KEY = Recording.class.getSimpleName();
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Recording createFromParcel(Parcel in) {
            return new Recording(in);
        }

        public Recording[] newArray(int size) {
            return new Recording[size];
        }
    };
    private String recording;
    private long recordingFileSize;
    private long id = -1;
    private Date date;
    private PitchRange range;
    private String name;

    public Recording(Date date) {
        setDate(date);
    }

    public Recording() {

    }

    protected Recording(Parcel src) {
        this.setDate(new Date(src.readLong()));
        this.setName(src.readString());
        this.setRange((PitchRange) src.readParcelable(PitchRange.class.getClassLoader()));
    }

    /**
     * formats recording's date & time to display anywhere in app
     *
     * @param context calling activity's context [to retrieve locale]
     * @return recording's date/time formatted according to device's locale
     */
    public String getDisplayDate(Context context) {
        return String.format("%s – %s", DateFormat.getDateFormat(context).format(this.getDate()), DateFormat.getTimeFormat(context).format(this.getDate()));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PitchRange getRange() {
        return range;
    }

    public void setRange(PitchRange range) {
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecording() {
        return recording;
    }

    public void setRecording(String recording) {
        this.recording = recording;
    }

    public long getRecordingFileSize() {
        return recordingFileSize;
    }

    public void setRecordingFileSize(long recordingFileSize) {
        this.recordingFileSize = recordingFileSize;
    }

    public Entry getGraphEntry()
    {
        return new Entry((float) this.getDate().getTime(), (float) this.getRange().getAvg());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.getDate().getTime());
        dest.writeString(this.getName());
        dest.writeParcelable(this.range, flags);
    }
}
