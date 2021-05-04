package de.lilithwittmann.voicepitchanalyzer.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import de.lilithwittmann.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.activities.RecordingListActivity;
import de.lilithwittmann.voicepitchanalyzer.callbacks.ItemTouchHelperCallback;
import de.lilithwittmann.voicepitchanalyzer.adapters.SwipeAdapter;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import de.lilithwittmann.voicepitchanalyzer.models.database.RecordingDB;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RecordingListFragment extends Fragment
{
    private List<Recording> recordings = new ArrayList<Recording>();
    private OnFragmentInteractionListener listener;

    /**
     * The fragment's ListView/GridView.
     */
    //    private ListView listView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SwipeAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordingListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RecordingDB recordingDB = new RecordingDB(getActivity());
        this.recordings = recordingDB.getRecordings();

        //        ArrayAdapter adapter = new ArrayAdapter<Recording>(getActivity(),
        //                android.R.layout.simple_list_item_activated_2, android.R.id.text2, this.recordings)
        //        {
        //            @Override
        //            public View getView(int position, View convertView, ViewGroup parent)
        //            {
        //                super.getView(position, convertView, parent);
        //
        //                if (convertView == null)
        //                {
        //                    convertView = getActivity().getLayoutInflater().inflate(
        //                            android.R.layout.simple_list_item_activated_2, parent, false);
        //                }
        //
        //                TextView largeText = (TextView) convertView.findViewById(android.R.id.text1);
        //                TextView smallText = (TextView) convertView.findViewById(android.R.id.text2);
        //
        //                Recording record = this.getItem(position);
        //                PitchRange range = record.getRange();
        //
        //                largeText.setText(record.getDisplayDate(getContext()));
        //                largeText.setTypeface(Typeface.DEFAULT_BOLD);
        //                smallText.setText(String.format(getResources().getString(R.string.min_max_avg),
        //                        Math.round(range.getMin()), Math.round(range.getMax()),
        //                        Math.round(range.getAvg())));
        //
        //                return convertView;
        //            }
        //        };
        //
        //        this.adapter = new SwipeActionAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recording_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this.getActivity().getApplicationContext(), this));

        this.adapter = new SwipeAdapter(getActivity().getApplicationContext(), (RecordingListActivity) getActivity(), this.recordings);
        recyclerView.setAdapter(this.adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(this.adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set the adapter
        //        listView = (ListView) view.findViewById(android.R.id.list);
        //        this.adapter.setListView((ListView) listView);
        //        listView.setAdapter(this.adapter);
        //
        //        this.adapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_swipe_bg)
        //                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_swipe_bg);
        //
        //        this.adapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener()
        //        {
        //            @Override
        //            public boolean hasActions(int i)
        //            {
        //                return true;
        //            }
        //
        //            @Override
        //            public boolean shouldDismiss(int i, int i1)
        //            {
        //                return false;
        //            }
        //
        //            @Override
        //            public void onSwipe(int[] ints, int[] ints1)
        //            {
        //                Log.i("test", "onSwipe()");
        //            }
        //        });

        //        SwipeMenuCreator creator = new SwipeMenuCreator()
        //        {
        //            @Override
        //            public void create(SwipeMenu swipeMenu)
        //            {
        //                // create "open" item
        //                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
        //                // set item background
        //                deleteItem.setBackground(R.color.action_bar_bg);
        //                // set item width
        //                deleteItem.setWidth(150);
        //                // set icon
        //                deleteItem.setIcon(R.drawable.ic_delete_white_48dp);
        //                // add to menu
        //                swipeMenu.addMenuItem(deleteItem);
        //            }
        //        };
        //
        //        this.listView.setMenuCreator(creator);
        //        this.listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        //        this.listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()
        //        {
        //            @Override
        //            public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index)
        //            {
        //                switch (index)
        //                {
        //                    case 0:
        //                        System.out.println("delete recording");
        //                        RecordingDB recordingDB = new RecordingDB(getActivity());
        //                        recordingDB.deleteRecording(recordings.get(position).getId());
        //                        //                        recordings.remove(position);
        //                        recordings.clear();
        //                        recordings.addAll(recordingDB.getRecordings());
        //                        break;
        //                }
        //
        //                return false;
        //            }
        //        });

        if (this.adapter.isEmpty())
        {
            System.out.println("visibility set to visible");
            view.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        }

        else
        {
            System.out.println("visibility set to invisible");
            view.findViewById(android.R.id.empty).setVisibility(View.GONE);
        }

        // Set OnItemClickListener so we can be notified on item clicks
        //        this.listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        RecordingDB recordingDB = new RecordingDB(getActivity());
        this.recordings.clear();
        this.recordings.addAll(recordingDB.getRecordings());
        //                ((SwipeMenuAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }


    //    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (null != listener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            listener.onFragmentInteraction(this.recordings.get(position).getId());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText)
    {
        //        View emptyView = listView.getEmptyView();
        //
        //        if (emptyView instanceof TextView)
        //        {
        //            ((TextView) emptyView).setText(emptyText);
        //        }
    }

//    @Override
//    public void onItemClick(View view, int position)
//    {
//        if (null != listener)
//        {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            listener.onFragmentInteraction(this.recordings.get(position).getId());
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        public void onFragmentInteraction(long recordID);
    }
}
