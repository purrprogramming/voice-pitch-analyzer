package lilithwittmann.de.voicepitchanalyzer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lilithwittmann.de.voicepitchanalyzer.models.Recording;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordingOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordingOverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Recording currentRecord;

    public RecordingOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @param recording
     * @return A new instance of fragment RecordingOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordingOverviewFragment newInstance(int sectionNumber, Recording recording) {
        RecordingOverviewFragment fragment = new RecordingOverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putParcelable(Recording.KEY, recording);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.currentRecord = this.getArguments().getParcelable(Recording.KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording_overview, container, false);
    }
}
