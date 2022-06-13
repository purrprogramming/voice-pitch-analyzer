package de.lilithwittmann.voicepitchanalyzer.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.stream.Stream;

import de.lilithwittmann.voicepitchanalyzer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {
    private static final String LOG_TAG = AboutFragment.class.getSimpleName();

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // enable text links for licences etc.
        Stream.of(R.id.apache, R.id.apache2, R.id.gpl, R.id.programming, R.id.design, R.id.contributors,
                        R.id.github, R.id.translation_dutch)
                .map(id -> (TextView) view.findViewById(id))
                .forEach(tv -> tv.setMovementMethod(android.text.method.LinkMovementMethod.getInstance()));
    }
}
