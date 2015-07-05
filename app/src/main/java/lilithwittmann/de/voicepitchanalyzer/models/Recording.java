package lilithwittmann.de.voicepitchanalyzer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
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
    private File recording;
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

    public File getRecording() {
        return recording;
    }

    public void setRecording(File recording) {
        this.recording = recording;
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
