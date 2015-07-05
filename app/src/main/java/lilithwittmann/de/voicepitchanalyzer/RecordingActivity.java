package lilithwittmann.de.voicepitchanalyzer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import lilithwittmann.de.voicepitchanalyzer.models.Recording;


public class RecordingActivity extends ActionBarActivity implements RecordingFragment.OnFragmentInteractionListener, RecordingListFragment.OnFragmentInteractionListener, RecordDetailViewFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = RecordingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
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
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new RecordingListFragment()).commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(long recordID) {

    }

    @Override
    public void onRecordFinished(long recordingID) {
        // TODO: open fragment w/ single recording

//            Log.i(LOG_TAG, "Date: " + recording.getDate());
//            Log.i(LOG_TAG, "Avg Pitch: " + recording.getRange().getAvg() + "Hz");
//            Log.i(LOG_TAG, "Max Pitch: " + recording.getRange().getMax() + "Hz");
//            Log.i(LOG_TAG, "Min Pitch: " + recording.getRange().getMin() + "Hz");

            Intent intent = new Intent(this, RecordViewActivity.class);
        intent.putExtra(Recording.KEY, recordingID);
            startActivity(intent);

//            Bundle bundle = new Bundle();
//            bundle.putParcelable(Recording.KEY, recording);
//            RecordDetailViewFragment recordFragment = new RecordDetailViewFragment();
//            recordFragment.setArguments(bundle);
//            getSupportFragmentManager().beginTransaction().replace(R.id.container, recordFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // nothing
    }
}
