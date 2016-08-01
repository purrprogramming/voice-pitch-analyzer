package de.lilithwittmann.voicepitchanalyzer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;

import com.github.mikephil.charting.data.Entry;
import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.fragments.ReadingFragment;
import de.lilithwittmann.voicepitchanalyzer.fragments.RecordGraphFragment;
import de.lilithwittmann.voicepitchanalyzer.fragments.RecordingFragment;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.List;


public class RecordingActivity extends ActionBarActivity implements RecordingFragment.OnFragmentInteractionListener, RecordGraphFragment.OnFragmentInteractionListener
{
    private static final String LOG_TAG = RecordingActivity.class.getSimpleName();
    private FragmentTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_recording);

        tabHost = (FragmentTabHost)findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        addTab(ReadingFragment.class, getString(R.string.title_reading));
        addTab(RecordGraphFragment.class, getString(R.string.title_section2));
        tabHost.setCurrentTab(0);
    }

    private void addTab(final Class view, String tag) {
        tag = tag.toUpperCase();

        View tabview = createStyledTabView(tabHost.getContext(), tag);
        TabHost.TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview);

        tabHost.addTab(setContent, view, null);
    }

    private static View createStyledTabView(final Context context, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);

        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        if (text.isEmpty())
            tv.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onCancel()
    {
        Intent intent = new Intent(this, RecordingListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRecordFinished(long recordingID)
    {
        Intent intent = new Intent(this, RecordViewActivity.class);
        intent.putExtra(Recording.KEY, recordingID);
        startActivity(intent);
    }

    public List<Entry> startingPitchEntries() {
        return new ArrayList<Entry>();
    }
}
