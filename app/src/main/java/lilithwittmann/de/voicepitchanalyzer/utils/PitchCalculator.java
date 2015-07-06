package lilithwittmann.de.voicepitchanalyzer.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lilith on 04/07/15.
 */
public class PitchCalculator {
    // Yes the range is very large - test it and maybe change to something like 85-255
    public static Double minPitch = 65.0;
    public static Double maxPitch = 300.0;
    public static Double minFemalePitch = 165.0;
    public static Double maxFemalePitch = 255.0;
    public static Double minMalePitch = 85.0;
    public static Double maxMalePitch = 180.0;
    private List<Double> pitches = new ArrayList<Double>();

    public static <T> List<T> copyList(List<T> source) {
        List<T> dest = new ArrayList<T>();
        for (T item : source) {
            dest.add(item);
        }
        return dest;
    }

    public Boolean addPitch(Double pitch) {
        if (pitch > minPitch && pitch < maxPitch) {
            this.pitches.add(pitch);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public List<Double> getPitches() {
        return this.pitches;
    }

    public Double calculatePitchAverage() {
        return this.calculateAverage(this.pitches);
    }

    public Double calculateMaxAverage() {
        List<Double> maxSorted = PitchCalculator.copyList(this.pitches);
        Collections.sort(maxSorted);
        Integer elements = maxSorted.size() / 3;
        return this.calculateAverage(maxSorted.subList(maxSorted.size() - elements, maxSorted.size()));
    }

    public Double calculateMinAverage() {
        List<Double> minSorted = PitchCalculator.copyList(this.pitches);
        Collections.reverse(minSorted);
        Integer elements = minSorted.size() / 3;
        return this.calculateAverage(minSorted.subList(minSorted.size() - elements, minSorted.size()));
    }

    private Double calculateAverage(List<Double> pitches) {
        Double sum = 0.0;
        if (!pitches.isEmpty()) {
            for (Double pitch : pitches) {
                sum += pitch;
            }
            return sum.doubleValue() / pitches.size();
        }
        return sum;
    }
}
