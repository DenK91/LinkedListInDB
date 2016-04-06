package com.denk.taskforglobus;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.denk.taskforglobus.data.DataProviderFragment;
import com.denk.taskforglobus.data.DataSetChangeListener;

/**
 * Task for Globus.
 */
public class MainActivity extends AppCompatActivity implements DataSetChangeListener {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_main);

        if (aSavedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new DataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DragOnLongPressFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }

    /**
     * Gets {@link DataSet}.
     * @return {@link DataSet}.
     */
    public DataSet getDataSet() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((DataProviderFragment) fragment).getDataSet();
    }

    /**
     * Notifies about data set was changed.
     */
    public void notifyDataSetChanged() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        if (fragment != null) {
            ((DragOnLongPressFragment) fragment).notifyDataSetChanged();
        }
    }

    @Override
    public void onDataSetChanged() {
        notifyDataSetChanged();
    }
}