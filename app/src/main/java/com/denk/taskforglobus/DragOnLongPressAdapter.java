package com.denk.taskforglobus;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.denk.taskforglobus.data.database.DataBaseItem;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

/**
 * Adapter to fill a {@link RecyclerView} elements which can be dragged.
 */
public class DragOnLongPressAdapter
        extends RecyclerView.Adapter<DragOnLongPressAdapter.ViewHolder>
        implements DraggableItemAdapter<DragOnLongPressAdapter.ViewHolder> {

    private DataSet mDataSet;

    /**
     * Constructor.
     *
     * @param aDataSet set of data items which should fill list.
     */
    public DragOnLongPressAdapter(DataSet aDataSet) {
        mDataSet = aDataSet;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int aPosition) {
        return mDataSet.getItem(aPosition).getId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup aParent, int aViewType) {
        final View v = LayoutInflater.from(aParent.getContext())
                .inflate(R.layout.list_item_draggable, aParent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder aViewHolder, int aPosition) {
        final DataBaseItem item = mDataSet.getItem(aPosition);
        aViewHolder.mTextView.setText(item.getValue());
        final int dragState = aViewHolder.getDragStateFlags();
        if ((dragState & DraggableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;
            if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;
                Drawable foreground = aViewHolder.mContainer.getForeground();
                if (foreground != null) {
                    foreground.setState(new int[] {});
                }
            } else if ((dragState & DraggableItemConstants.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }
            aViewHolder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.getSize();
    }

    @Override
    public void onMoveItem(int aFrom, int aTo) {
        if (mDataSet != null) {
            mDataSet.onMove(aFrom, aTo);
        }
        //notifyItemMoved(aFrom, aTo);
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder aHolder, int aPosition, int aX, int aY) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder aHolder, int aPosition) {
        return null;
    }

    /**
     * View holder.
     */
    public static class ViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public TextView mTextView;

        public ViewHolder(View aView) {
            super(aView);
            mContainer = (FrameLayout) aView.findViewById(R.id.container);
            mTextView = (TextView) aView.findViewById(android.R.id.text1);
        }
    }
}
