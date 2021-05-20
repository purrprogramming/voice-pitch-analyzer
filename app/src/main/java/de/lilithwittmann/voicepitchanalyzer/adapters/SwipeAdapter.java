package de.lilithwittmann.voicepitchanalyzer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.activities.RecordingListActivity;
import de.lilithwittmann.voicepitchanalyzer.models.PitchRange;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import de.lilithwittmann.voicepitchanalyzer.models.database.RecordingDB;
import de.lilithwittmann.voicepitchanalyzer.utils.RecordingPaths;

/**
 * Created by Yuri on 22-09-15
 */

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.SwipeViewHolder>
{
    private static final String LOG_TAG = SwipeAdapter.class.getSimpleName();

    private static List<Recording> recordings;
    private RecordingListActivity activity;
    private Context context;
    private SwipeViewHolder viewHolder;
    private Recording deletedRecord;
    private int itemPosition;

    public SwipeAdapter(Context context, RecordingListActivity activity, List<Recording> list)
    {
        this.context = context;
        this.activity = activity;
        this.recordings = list;
    }

    @Override
    public SwipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;

        Log.i("SwipeAdapter", String.format("Build Version: %s", Build.VERSION.SDK_INT));
        Log.i("SwipeAdapter", String.format("Lollipop Version: %s", Build.VERSION_CODES.LOLLIPOP));

        // create a new view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Log.i("SwipeAdapter", String.format("API level larger than %s", Build.VERSION_CODES.LOLLIPOP));
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listview, parent, false);
        }

        else
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        SwipeViewHolder vh = new SwipeViewHolder(view, this.activity);
        view.setOnClickListener(vh);

        return vh;
    }

    @Override
    public void onBindViewHolder(SwipeViewHolder holder, int position)
    {
        Recording record = this.recordings.get(position);
        PitchRange range = record.getRange();
        holder.setLargeText(record.getDisplayDate(this.context));
        holder.getLargeText().setTypeface(Typeface.DEFAULT_BOLD);
        holder.setSmallText(String.format(this.context.getResources().getString(R.string.min_max_avg),
                Math.round(range.getMin()), Math.round(range.getMax()),
                Math.round(range.getAvg())));

        this.viewHolder = holder;
    }

    @Override
    public int getItemCount()
    {
        return this.recordings.size();
    }

    public boolean isEmpty()
    {
        if (this.recordings == null || this.getItemCount() == 0)
        {
            return true;
        }

        else
        {
            return false;
        }
    }

    public void onItemDismiss(int position)
    {
        // remove item
        this.deletedRecord = this.recordings.get(position);
        this.itemPosition = position;

        this.recordings.remove(position);
        notifyItemRemoved(position);

        final View coordinatorLayoutView = this.activity.findViewById(R.id.container);

        final View.OnClickListener clickListener = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.i("SwipeAdapter", "onClick() -- undo delete");
                recordings.add(itemPosition, deletedRecord);
                notifyItemInserted(itemPosition);
            }
        };

        Snackbar
                .make(coordinatorLayoutView, R.string.record_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, clickListener)
                .setActionTextColor(this.activity.getResources().getColor(R.color.canvas_light))
                .setCallback(new Snackbar.Callback()
                {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event)
                    {
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION)
                        {
                            deleteRecording(activity, deletedRecord);
                        }
                        //            switch (event)
                        //            {
                        //                case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        //                    break;
                        //            }

                        Log.i("SwipeAdapter", "item should now actually be deleted from DB");
                        // RecordingDB db = new RecordingDB(activity);
                        // db.deleteRecording(deletedRecord.getId());
                    }
                })
                .show();
    }

    private static void deleteRecording(Context context, Recording recording)
    {
        RecordingDB db = new RecordingDB(context);
        db.deleteRecording(recording.getId());

        if (recording.getRecording() != null)
        {
            // on deletion failure, this file will eventually be cleaned up by RecordingCleaner
            Path recordingPath = RecordingPaths.getRecordingPath(context, recording.getRecording());
            if (recordingPath != null)
            {
                try
                {
                    Files.delete(recordingPath);
                } catch (IOException ex)
                {
                    Log.w(LOG_TAG, "error deleting recording " + recordingPath + ": " + ex);
                }
            }
            else
            {
                Log.w(LOG_TAG, "could not determine path to delete recording " + recording.getRecording());
            }
        }
    }

    //    public void onItemMove(int fromPosition, int toPosition) {
    //        String prev = this.recordings.remove(fromPosition);
    //        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
    //        notifyItemMoved(fromPosition, toPosition);

    public static class SwipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private RecordingListActivity activity;
        private TextView largeText;
        private TextView smallText;

        public TextView getLargeText()
        {
            return largeText;
        }

        public void setLargeText(String text)
        {
            this.getLargeText().setText(text);
        }

        public TextView getSmallText()
        {
            return smallText;
        }

        public void setSmallText(String text)
        {
            this.smallText.setText(text);
        }

        public SwipeViewHolder(View itemView, RecordingListActivity activity)
        {
            super(itemView);

            itemView.setOnClickListener(this);

            this.activity = activity;
            this.largeText = (TextView) itemView.findViewById(android.R.id.text1);
            this.smallText = (TextView) itemView.findViewById(android.R.id.text2);
        }

        @Override
        public void onClick(View v)
        {
            activity.onFragmentInteraction(recordings.get(this.getAdapterPosition()).getId());
        }

        public void onItemSelected()
        {
            //            itemView.setBackgroundColor(Color.LTGRAY);
        }

        public void onItemClear()
        {
            itemView.setBackgroundColor(0);
        }
    }
}
