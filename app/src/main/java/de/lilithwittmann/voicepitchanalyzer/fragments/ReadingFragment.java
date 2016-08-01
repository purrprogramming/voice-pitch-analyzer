package de.lilithwittmann.voicepitchanalyzer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.models.Texts;

import java.util.Locale;

/**
 * Reading material for display while doing a recording.
 */
public class ReadingFragment extends Fragment {
    public ReadingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reading, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Texts texts = new Texts();
        Context context = getActivity();
        String lang = "en";
        if (texts.supportsLocale(Locale.getDefault().getLanguage()) == Boolean.TRUE) {
            lang = Locale.getDefault().getLanguage();
        }

        // get current textNumber
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Integer textNumber = sharedPref.getInt(getString(R.string.saved_text_number), 1);


        // calculate next textNumber
        Integer nextText = 1;
        if (textNumber + 1 < texts.countTexts(lang)) {
            nextText = textNumber + 1;
        } else if (textNumber > texts.countTexts(lang)) {
            //really special case - if the user changes the device language and in the new language there are
            // less text samples available than in the previous one, set the text number back to one
            textNumber = 1;
            nextText = 2;
        }

        //save next textNumber
        sharedPref.edit().putInt(getString(R.string.saved_text_number), nextText).apply();

        //render text
        final TextView recording_text = (TextView) view.findViewById(R.id.recording_text);
        recording_text.setText(texts.getText(lang, textNumber));
    }
}
