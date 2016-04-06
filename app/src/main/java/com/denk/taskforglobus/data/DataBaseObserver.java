package com.denk.taskforglobus.data;

import android.database.ContentObserver;
import android.os.Handler;

import com.denk.taskforglobus.DataSet;
import com.denk.taskforglobus.data.database.DataBaseList;

/**
 * Can update {@link DataSet} when data base was changed.
 */
class DataBaseObserver extends ContentObserver {

    private DataBaseList mDbList;
    private DataSet mDataSet;

    /**
     * Constructor.
     *
     * @param aHandler {@link Handler}.
     * @param aDbList {@link DataBaseList} to get actually data.
     * @param aDataSet {@link DataSet} to set actually data.
     */
    DataBaseObserver(Handler aHandler, DataBaseList aDbList, DataSet aDataSet) {
        super(aHandler);
        mDbList = aDbList;
        mDataSet = aDataSet;
    }

    @Override
    public void onChange(boolean aSelfChange) {
        super.onChange(aSelfChange);
        mDataSet.updateDataSet(mDbList.getList());
    }
}
