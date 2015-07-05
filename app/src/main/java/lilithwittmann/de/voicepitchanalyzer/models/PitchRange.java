package lilithwittmann.de.voicepitchanalyzer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 04/07/15.
 */
public class PitchRange implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PitchRange createFromParcel(Parcel in) {
            return new PitchRange(in);
        }

        public PitchRange[] newArray(int size) {
            return new PitchRange[size];
        }
    };
    private double avg;
    private double min;
    private double max;
    private List<Double> pitches = new ArrayList<Double>();

    public PitchRange() {
    }

    protected PitchRange(Parcel src) {
        this.setAvg(src.readDouble());
        this.setMin(src.readDouble());
        this.setMax(src.readDouble());
        this.setPitches(src.readArrayList(Double.class.getClassLoader()));
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public List<Double> getPitches() {
        return pitches;
    }

    public void setPitches(List<Double> pitches) {
        this.pitches = pitches;
    }

    public List<Entry> getGraphEntries() {
        List<Entry> list = new ArrayList<Entry>();

        for (int i = 0; i < this.getPitches().size(); i++) {
            list.add(new Entry(this.getPitches().get(i).floatValue(), i));
        }

        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.getAvg());
        dest.writeDouble(this.getMin());
        dest.writeDouble(this.getMax());
        dest.writeList(this.getPitches());
    }
}