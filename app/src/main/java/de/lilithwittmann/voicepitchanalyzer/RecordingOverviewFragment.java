package de.lilithwittmann.voicepitchanalyzer;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import de.lilithwittmann.voicepitchanalyzer.utils.PitchCalculator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordingOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordingOverviewFragment extends Fragment implements SurfaceHolder.Callback
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    SurfaceView gradient;
    SurfaceHolder gradientHolder;

    public RecordingOverviewFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @return A new instance of fragment RecordingOverviewFragment.
     */
    public static RecordingOverviewFragment newInstance(int sectionNumber)
    {
        RecordingOverviewFragment fragment = new RecordingOverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        this.gradient = (SurfaceView) view.findViewById(R.id.gradient_canvas);
        this.gradientHolder = this.gradient.getHolder();
        this.gradientHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        Canvas canvas = surfaceHolder.lockCanvas();
        Double pitchRange = PitchCalculator.maxPitch - PitchCalculator.minPitch;
        Double pxPerHz = this.gradient.getHeight() / pitchRange;

        Paint p = new Paint();
        Paint textPaint = new Paint();

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);

        p.setColor(getResources().getColor(R.color.canvas_bg_dark));
        p.setAlpha(128);
        textPaint.setColor(getResources().getColor(R.color.black));
        textPaint.setAlpha(255);
        canvas.drawARGB(255, 255, 255, 255);
        p.setStrokeWidth(10);
        //draw female pitch
        canvas.drawRect(0,
                this.gradient.getBottom() - (float) ((PitchCalculator.minFemalePitch - PitchCalculator.minPitch) * pxPerHz),
                this.gradient.getWidth(),
                this.gradient.getBottom() - (float) ((PitchCalculator.maxFemalePitch - PitchCalculator.minPitch) * pxPerHz),
                p
        );

        canvas.drawText(getResources().getString(R.string.female_range), this.gradient.getWidth() - 150, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxFemalePitch - PitchCalculator.minPitch) * pxPerHz) + 20, textPaint);
        //draw male pitch
        p.setColor(getResources().getColor(R.color.canvas_bg_light));
        p.setAlpha(128);
        textPaint.setColor(getResources().getColor(R.color.black));
        textPaint.setAlpha(255);
        canvas.drawRect(0,
                this.gradient.getHeight() - (float) ((PitchCalculator.minMalePitch - PitchCalculator.minPitch) * pxPerHz),
                this.gradient.getWidth(),
                this.gradient.getHeight() - (float) ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) * pxPerHz),
                p
        );

        canvas.drawText(getResources().getString(R.string.male_range), this.gradient.getWidth() - 125, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch -
                        (PitchCalculator.maxMalePitch - PitchCalculator.minFemalePitch)) * pxPerHz) + 20, textPaint);


        //draw androgynous label
        canvas.drawText(getResources().getString(R.string.androgynous_range), this.gradient.getWidth() - 190, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) * pxPerHz) + 20, textPaint);
        //draw pitch labels
        textPaint.setTextSize(20);
        //min_male
        canvas.drawText(String.valueOf(PitchCalculator.minMalePitch), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.minMalePitch - PitchCalculator.minPitch) * pxPerHz) - 20, textPaint);

        //max_male
        canvas.drawText(String.valueOf(PitchCalculator.maxMalePitch), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) * pxPerHz) - 20, textPaint);


        //avg_male
        canvas.drawText(String.valueOf(PitchCalculator.minMalePitch + ((PitchCalculator.minFemalePitch - PitchCalculator.minMalePitch) / 2)), 10, this.gradient.getHeight() - (float)
                (((PitchCalculator.minMalePitch + (PitchCalculator.minFemalePitch - PitchCalculator.minMalePitch) / 2) - PitchCalculator.minPitch)* pxPerHz) + 10, textPaint);

        //min_female
        canvas.drawText(String.valueOf(PitchCalculator.minFemalePitch), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.minFemalePitch - PitchCalculator.minPitch) * pxPerHz) + 35, textPaint);


        //max_female
        canvas.drawText(String.valueOf(PitchCalculator.maxFemalePitch), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxFemalePitch - PitchCalculator.minPitch) * pxPerHz) + 35, textPaint);

        //avg_female
        canvas.drawText(String.valueOf(PitchCalculator.maxMalePitch + ((PitchCalculator.maxFemalePitch - PitchCalculator.maxMalePitch) / 2)), 10, this.gradient.getHeight() - (float)
                (((PitchCalculator.maxMalePitch + (PitchCalculator.maxFemalePitch - PitchCalculator.maxMalePitch) / 2) - PitchCalculator.minPitch)* pxPerHz) + 10, textPaint);


        //average
        canvas.drawText(String.valueOf((PitchCalculator.maxMalePitch + PitchCalculator.minFemalePitch)/2),
                10, this.gradient.getHeight() - (float)
                        (((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) -
                                (PitchCalculator.maxMalePitch - PitchCalculator.minFemalePitch)/2) * pxPerHz) +10 , textPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        //draw range
//        paint.setStrokeWidth((float) ((RecordViewActivity.currentRecord.getRange().getMax() - RecordViewActivity.currentRecord.getRange().getMin()) * pxPerHz));
        paint.setAlpha(120);
        canvas.drawRect(0,
                this.gradient.getBottom() - (float) ((RecordViewActivity.currentRecord.getRange().getMin() - PitchCalculator.minPitch) * pxPerHz),
                this.gradient.getWidth(),
                this.gradient.getBottom() - (float) ((RecordViewActivity.currentRecord.getRange().getMax() - PitchCalculator.minPitch) * pxPerHz),
                paint);
        paint.setStrokeWidth(10);
        paint.setAlpha(255);
        paint.setColor(Color.BLACK);
        canvas.drawLine(0,
                this.gradient.getHeight() - (float) ((RecordViewActivity.currentRecord.getRange().getAvg()-PitchCalculator.minPitch) * pxPerHz),
                this.gradient.getWidth(),
                this.gradient.getHeight() - (float) ((RecordViewActivity.currentRecord.getRange().getAvg()-PitchCalculator.minPitch) * pxPerHz),
                paint);

        textPaint.setColor(Color.BLACK);
        textPaint.setAlpha(120);
        canvas.drawText(getResources().getString(R.string.your_range), 10,
                this.gradient.getHeight() - (float) ((RecordViewActivity.currentRecord.getRange().getMin() - PitchCalculator.minPitch) * pxPerHz) + 35,
                textPaint);

        surfaceHolder.unlockCanvasAndPost(canvas);
        Log.d("foo", String.valueOf(this.gradient.getHeight() - (float) (PitchCalculator.maxFemalePitch * pxPerHz)));
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {

    }
}
