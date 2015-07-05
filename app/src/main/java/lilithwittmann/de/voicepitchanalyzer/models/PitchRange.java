package lilithwittmann.de.voicepitchanalyzer.models;

import java.util.LinkedList;

/**
 * Created by Yuri on 04/07/15.
 */
public class PitchRange {
    private double min;
    private double max;
    private double avg;
    private LinkedList<Double> pitches = new LinkedList<Double>();

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

    public LinkedList<Double> getPitches() {
        return pitches;
    }

    public void setPitches(LinkedList<Double> pitches) {
        this.pitches = pitches;
    }
}
