package de.lilithwittmann.voicepitchanalyzer.callbacks;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import de.lilithwittmann.voicepitchanalyzer.adapters.SwipeAdapter;

/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to react to {@link
 * SwipeAdapter} callbacks and the <code>RecyclerView.SwipeViewHolder</code> to implement
 * {@link SwipeAdapter.SwipeViewHolder}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback
{

    private final SwipeAdapter mAdapter;

    public ItemTouchHelperCallback(SwipeAdapter adapter)
    {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled()
    {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target)
    {
        //            mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i)
    {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
    {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
        {
            SwipeAdapter.SwipeViewHolder itemViewHolder = (SwipeAdapter.SwipeViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);

        SwipeAdapter.SwipeViewHolder itemViewHolder = (SwipeAdapter.SwipeViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }
}
