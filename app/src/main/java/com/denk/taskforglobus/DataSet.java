package com.denk.taskforglobus;

import com.denk.taskforglobus.data.database.DataBaseItem;

import java.util.List;

/**
 * Data set for adapter.
 */
public abstract class DataSet {

    /**
     * Moves element in data set.
     *
     * @param aFrom from position.
     * @param aTo to position.
     */
    public abstract void onMove(int aFrom, int aTo);

    /**
     * Gets data size.
     *
     * @return data size.
     */
    public abstract int getSize();

    /**
     * Gets data item with specific index.
     * @param aIndex item index in data set.
     * @return {@link DataBaseItem}.
     */
    public abstract DataBaseItem getItem(int aIndex);

    /**
     * Updates data set.
     *
     * @param aData new data.
     */
    public abstract void updateDataSet(List<DataBaseItem> aData);
}
