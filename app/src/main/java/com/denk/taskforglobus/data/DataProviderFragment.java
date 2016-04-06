package com.denk.taskforglobus.data;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.denk.taskforglobus.DataSet;
import com.denk.taskforglobus.MainActivity;
import com.denk.taskforglobus.data.database.DataBaseList;
import com.denk.taskforglobus.data.database.DataBaseProvider;

/**
 * Invisible fragment to keep objects which work with data base.
 */
public class DataProviderFragment extends Fragment {

    private static final String DEFAULT_DATA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private DataSet mDataSet;
    private DataBaseObserver mObserver;

    @Override
    public void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);

        setRetainInstance(true);

        DataBaseList dataBaseList = new DataBaseList(getContext());
        if (dataBaseList.getListSize() == 0) {
            for (int j = 0; j < DEFAULT_DATA.length(); j++) {
                final String text = Character.toString(DEFAULT_DATA.charAt(j));
                dataBaseList.addToEnd(text);
            }
        }

        mDataSet = new DataProvider(((MainActivity)getActivity()), dataBaseList);
        mObserver = new DataBaseObserver(null, dataBaseList, mDataSet);
        getContext().getContentResolver()
                .registerContentObserver(DataBaseProvider.LIST_CONTENT_URI, true, mObserver);
    }

    @Override
    public void onDestroy() {
        getContext().getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    /**
     * Gets {@link DataSet}.
     *
     * @return {@link DataSet}.
     */
    public DataSet getDataSet() {
        return mDataSet;
    }
}
