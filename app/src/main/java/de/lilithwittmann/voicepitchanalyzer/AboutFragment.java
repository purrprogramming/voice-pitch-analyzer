package de.lilithwittmann.voicepitchanalyzer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lilithwittmann.de.voicepitchanalyzer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment
{
    private static final String LOG_TAG = AboutFragment.class.getSimpleName();

    public AboutFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // enable text links for licences
        TextView apacheLicence = (TextView) view.findViewById(R.id.apache);
        TextView gpl = (TextView) view.findViewById(R.id.gpl);

        apacheLicence.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        gpl.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }
}
