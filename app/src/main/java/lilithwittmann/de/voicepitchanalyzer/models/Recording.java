package lilithwittmann.de.voicepitchanalyzer.models;

import java.io.File;
import java.util.Date;

/**
 * Created by Yuri on 04/07/15.
 */
public class Recording {
    private File recording;
    private Date date;
    private PitchRange range;
    private String name;

    public Recording(Date date) {
        setDate(date);
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
}
