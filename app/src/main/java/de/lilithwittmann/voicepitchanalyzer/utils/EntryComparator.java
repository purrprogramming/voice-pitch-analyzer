package de.lilithwittmann.voicepitchanalyzer.utils;

import com.github.mikephil.charting.data.Entry;

import java.util.Comparator;

/**
 * Created by Yuri on 21-08-2016
 */
public class EntryComparator implements Comparator<Entry>
{
    @Override
    public int compare(Entry lhs, Entry rhs)
    {
        return (lhs.getXIndex() > rhs.getXIndex() ? 1 : -1);
    }
}
