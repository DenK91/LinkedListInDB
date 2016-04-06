package com.denk.taskforglobus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

/**
 * Main fragment with list.
 */
public class DragOnLongPressFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    /**
     * Constructor.
     */
    public DragOnLongPressFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater aInflater,
                             @Nullable ViewGroup aContainer,
                             @Nullable Bundle aSavedInstanceState) {
        return aInflater.inflate(R.layout.fragment_recycler_list_view, aContainer, false);
    }

    @Override
    public void onViewCreated(View aView, @Nullable Bundle aSavedInstanceState) {
        super.onViewCreated(aView, aSavedInstanceState);

        mRecyclerView = (RecyclerView) aView.findViewById(R.id.recycler_view);

        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        final DragOnLongPressAdapter myItemAdapter = new DragOnLongPressAdapter(
                ((MainActivity) getActivity()).getDataSet());
        mAdapter = myItemAdapter;
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.addItemDecoration(
                new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(),
                        R.drawable.list_divider_h), true));
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    @Override
    public void onPause() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.cancelDrag();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

    /**
     * Notify that the data set has changed.
     */
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
