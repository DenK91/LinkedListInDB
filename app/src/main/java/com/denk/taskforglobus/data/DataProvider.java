package com.denk.taskforglobus.data;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.denk.taskforglobus.DataSet;
import com.denk.taskforglobus.data.database.DataBaseItem;
import com.denk.taskforglobus.data.database.DataBaseList;

import java.util.LinkedList;
import java.util.List;

/**
 * Data provider for adapter.
 */
class DataProvider extends DataSet implements Handler.Callback {

    private static final int HANDLER_MSG_UPDATE = 1;

    private List<DataBaseItem> mData;
    private Handler mHandler;
    private DataSetChangeListener mDataSetChangeListener;
    private DataBaseList mDataBaseList;

    /**
     * Constructor.
     *
     * @param aDataSetChangeListener {@link DataSetChangeListener}.
     * @param aDataBaseList {@link DataBaseList}.
     */
    DataProvider(DataSetChangeListener aDataSetChangeListener, DataBaseList aDataBaseList) {
        mDataSetChangeListener = aDataSetChangeListener;
        mDataBaseList = aDataBaseList;
        mData = new LinkedList<>();
        updateData(aDataBaseList.getList());
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public void updateDataSet(List<DataBaseItem> aData) {
        Message message = mHandler.obtainMessage(HANDLER_MSG_UPDATE, aData);
        message.sendToTarget();
    }

    @Override
    public int getSize() {
        return mData.size();
    }

    @Override
    public DataBaseItem getItem(int aIndex) {
        if (aIndex < 0 || aIndex >= getSize()) {
            throw new IndexOutOfBoundsException("index = " + aIndex);
        }
        return mData.get(aIndex);
    }

    @Override
    public void onMove(int aFrom, int aTo) {
        if (aFrom == aTo) {
            return;
        }
        final DataBaseItem item = mData.remove(aFrom);
        mData.add(aTo, item);
        if (mDataBaseList != null) {
            mDataBaseList.move(aFrom + 1, aTo + 1);
        }
    }

    @Override
    public boolean handleMessage(Message aMsg) {
        boolean isMessageHandled = true;
        try {
            switch (aMsg.what) {
                case HANDLER_MSG_UPDATE:
                    updateData(aMsg.obj != null ? (List<DataBaseItem>) aMsg.obj : null);
                    break;
                default:
                    isMessageHandled = false;
                    break;
            }
        } catch (Exception e) {
            isMessageHandled = false;
        }
        return isMessageHandled;
    }

    /**
     * Updates data set.
     *
     * @param aData new data set.
     */
    private void updateData(List<DataBaseItem> aData) {
        if (!mData.equals(aData)) {
            if (aData != null) {
                mData.clear();
                mData.addAll(aData);
            } else {
                mData.clear();
            }
            if (mDataSetChangeListener != null) {
                mDataSetChangeListener.onDataSetChanged();
            }
        }
    }
}