package lilithwittmann.de.voicepitchanalyzer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import lilithwittmann.de.voicepitchanalyzer.models.Recording;


public class RecordingActivity extends ActionBarActivity implements RecordingFragment.OnFragmentInteractionListener, RecordingListFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = RecordingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new RecordingFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case (R.id.action_settings): {
                return true;
            }
            case (R.id.action_record): {
                getFragmentManager().beginTransaction().replace(R.id.container, new RecordingListFragment()).commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onRecordFinished(Recording recording) {
        // TODO: open fragment w/ single recording

        if (recording != null) {
            Log.i(LOG_TAG, "Date: " + recording.getDate());
            Log.i(LOG_TAG, "Avg Pitch: " + recording.getRange().getAvg() + "Hz");
            Log.i(LOG_TAG, "Max Pitch: " + recording.getRange().getMax() + "Hz");
            Log.i(LOG_TAG, "Min Pitch: " + recording.getRange().getMin() + "Hz");
        }
    }
}
