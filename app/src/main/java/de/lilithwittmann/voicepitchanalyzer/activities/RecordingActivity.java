package de.lilithwittmann.voicepitchanalyzer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.crashlytics.android.Crashlytics;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.fragments.RecordingFragment;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import io.fabric.sdk.android.Fabric;


public class RecordingActivity extends ActionBarActivity implements RecordingFragment.OnFragmentInteractionListener /*, RecordingListFragment.OnFragmentInteractionListener, RecordGraphFragment.OnFragmentInteractionListener*/
{
    private static final String LOG_TAG = RecordingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_recording);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RecordingFragment())
                    .commit();
        }
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
}
