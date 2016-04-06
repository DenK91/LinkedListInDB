package com.denk.taskforglobus.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper on linked list database.
 * It helps to present the database as list.
 */
public class DataBaseList implements Handler.Callback {

    private static final String TAG = "DataBaseList";

    private static final int HANDLER_MSG_ADD = 1;
    private static final int HANDLER_MSG_MOVE = 2;

    private Context mContext;

    /**
     * Handler for doing operations which can change database in the separate thread.
     */
    private Handler mHandler;

    /**
     * Constructor.
     *
     * @param aContext {@link Context}.
     */
    public DataBaseList(Context aContext) {
        DatabaseManager.initializeInstance(aContext);
        mContext = aContext;
        HandlerThread thread = new HandlerThread("TestApp.DataBaseList",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mHandler = new Handler(thread.getLooper(), this);
    }

    @Override
    public boolean handleMessage(Message aMsg) {
        boolean isMessageHandled = true;
        try {
            switch (aMsg.what) {
                case HANDLER_MSG_ADD:
                    addToEndList((String)aMsg.obj);
                    break;
                case HANDLER_MSG_MOVE:
                    moveItem(aMsg.arg1, aMsg.arg2);
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
     * Gets size of list.
     *
     * @return size of list.
     */
    public int getListSize() {
        return getCount(DatabaseManager.getInstance().getDatabase());
    }

    /**
     * Adds value to list.
     * <p>
     * <b>Work on separate thread.</b>
     *
     * @param aValue value for adding.
     */
    public void addToEnd(String aValue) {
        sendMsgForAdd(aValue);
    }

    /**
     * Gets list of {@link DataBaseItem}s.
     *
     * @return list of {@link DataBaseItem}s.
     */
    public List<DataBaseItem> getList() {
        List<DataBaseItem> list = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().getDatabase();
        try {
            db.beginTransaction();
            int steps = getCount(db);
            int itemId = getLastItemId(db);
            while (steps > 0) {
                list.add(new DataBaseItem(itemId, getValueById(db, itemId)));
                itemId = getItemIdByLink(db, itemId);
                steps--;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d(TAG, ex.getClass() + " error: " + ex.getMessage());
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * Moves list item from one position to another.
     * <p>
     * <b>Work on separate thread.</b>
     *
     * @param aFrom position from.
     * @param aTo position to.
     */
    public void move(int aFrom, int aTo) {
        sendMsgForMove(aFrom, aTo);
    }

    /**
     * Moves list item  from one position to another.
     *
     * @param aFrom position from.
     * @param aTo position to.
     */
    private void moveItem(int aFrom, int aTo) {
        SQLiteDatabase db = DatabaseManager.getInstance().getDatabase();
        try {
            db.beginTransaction();
            if (aFrom != aTo) {
                int itemIdFrom = getItemIdByPosition(db, aFrom);
                int itemIdTo = getItemIdByPosition(db, aTo);
                if (itemIdFrom != 0 && itemIdTo != 0) {
                    int prevFromId = getItemIdByLink(db, itemIdFrom);
                    int nextFromId = getLinkById(db, itemIdFrom);
                    if (prevFromId != 0) {
                        updateItemLink(db, prevFromId, nextFromId);
                    }
                    if (aFrom < aTo) {
                        int nextToId = getLinkById(db, itemIdTo);
                        updateItemLink(db, itemIdFrom, nextToId);
                        updateItemLink(db, itemIdTo, itemIdFrom);
                    } else {
                        int prevToId = getItemIdByLink(db, itemIdTo);
                        updateItemLink(db, prevToId, itemIdFrom);
                        updateItemLink(db, itemIdFrom, itemIdTo);
                    }
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d(TAG, ex.getClass() + " error: " + ex.getMessage());
        }
        mContext.getContentResolver().notifyChange(DataBaseProvider.LIST_CONTENT_URI, null);
    }

    /**
     * Adds value to list.
     *
     * @param aValue value for adding.
     */
    private void addToEndList(String aValue) {
        SQLiteDatabase db = DatabaseManager.getInstance().getDatabase();
        try {
            db.beginTransaction();
            int listSize = getCount(db);
            if (listSize > 0) {
                int lastItemId = getLastItemId(db);
                if (lastItemId != 0) {
                    int newItemId = addNewListItem(db, 0, aValue);
                    updateItemLink(db, lastItemId, newItemId);
                }
            } else {
                addNewListItem(db, 0, aValue);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d(TAG, ex.getClass() + " error: " + ex.getMessage());
        }
        mContext.getContentResolver().notifyChange(DataBaseProvider.LIST_CONTENT_URI, null);
    }

    /**
     * Gets count of row in the linked list table.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @return count of row in the linked list table.
     */
    private int getCount(SQLiteDatabase aDb) {
        Cursor countCursor = aDb.query(DataBaseContract.TABLE_NAME,
                new String[]{"count(*)"}, null, null, null, null, null);
        if (countCursor == null) {
            return 0;
        }
        countCursor.moveToFirst();
        int count = countCursor.getInt(0);
        countCursor.close();
        return count;
    }

    /**
     * Insert new row to linked list table.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @param aLink value of link column.
     * @param aValue value of value column.
     * @return id of inserted row.
     */
    private int addNewListItem(SQLiteDatabase aDb, int aLink, String aValue) {
        ContentValues cv = new ContentValues();
        cv.put(DataBaseContract.LINK, aLink);
        cv.put(DataBaseContract.VALUE, aValue);
        return (int) aDb.insert(DataBaseContract.TABLE_NAME, null, cv);
    }

    /**
     * Updates link in the row.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @param aItemId id of row.
     * @param aLink new link value.
     */
    private void updateItemLink(SQLiteDatabase aDb, int aItemId, int aLink) {
        ContentValues cv = new ContentValues();
        cv.put(DataBaseContract.LINK, aLink);
        aDb.update(DataBaseContract.TABLE_NAME, cv,
                BaseColumns._ID + " = " + aItemId, null);
    }

    /**
     * Gets id row of last list item from database.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @return id row of last list item from database.
     */
    private int getLastItemId(SQLiteDatabase aDb) {
        int idLastItem = 0;
        Cursor cursorWithLastListItem = aDb.query(DataBaseContract.TABLE_NAME,
                new String[]{BaseColumns._ID},
                DataBaseContract.LINK + " = 0",
                null, null, null, null);
        if (cursorWithLastListItem != null) {
            //Should be only one item.
            if (cursorWithLastListItem.getCount() == 1) {
                cursorWithLastListItem.moveToFirst();
                int idColIndex = cursorWithLastListItem.getColumnIndex(BaseColumns._ID);
                idLastItem = cursorWithLastListItem.getInt(idColIndex);
            }
            cursorWithLastListItem.close();
        }
        return idLastItem;
    }

    /**
     * Gets id row of list item from database with specific position.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @param aPosition position.
     * @return id row of list item from database with specific position.
     */
    private int getItemIdByPosition(SQLiteDatabase aDb, int aPosition) {
        int itemId = 0;
        int listSize = getCount(aDb);
        if (aPosition > 0 && aPosition <= listSize) {
            int steps = listSize - aPosition;
            itemId = getLastItemId(aDb);
            while (steps > 0) {
                itemId = getItemIdByLink(aDb, itemId);
                steps--;
            }
        }
        return itemId;
    }

    /**
     * Gets id row of list item from database with specific link.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @param aLink link.
     * @return id row of list item from database with specific link.
     */
    private int getItemIdByLink(SQLiteDatabase aDb, int aLink) {
        int id = 0;
        Cursor cursorWithItem = aDb.query(DataBaseContract.TABLE_NAME,
                new String[]{BaseColumns._ID},
                DataBaseContract.LINK + " = " + aLink,
                null, null, null, null);
        //Should be only one item.
        if (cursorWithItem != null) {
            if (cursorWithItem.getCount() == 1) {
                cursorWithItem.moveToFirst();
                int idColIndex = cursorWithItem.getColumnIndex(BaseColumns._ID);
                id = cursorWithItem.getInt(idColIndex);
            }
            cursorWithItem.close();
        }
        return id;
    }

    /**
     * Gets link from database with specific id.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @param aId id.
     * @return link from database with specific id.
     */
    private int getLinkById(SQLiteDatabase aDb, int aId) {
        int link = -1;
        Cursor cursorWithItem = aDb.query(DataBaseContract.TABLE_NAME,
                new String[]{DataBaseContract.LINK},
                BaseColumns._ID + " = " + aId,
                null, null, null, null);
        if (cursorWithItem != null) {
            //Should be only one item.
            if (cursorWithItem.getCount() == 1) {
                cursorWithItem.moveToFirst();
                int linkColIndex = cursorWithItem.getColumnIndex(DataBaseContract.LINK);
                link = cursorWithItem.getInt(linkColIndex);
            }
            cursorWithItem.close();
        }
        return link;
    }

    /**
     * Gets value from database with specific id.
     *
     * @param aDb {@link SQLiteDatabase} instance.
     * @param aId id.
     * @return value from database with specific id.
     */
    private String getValueById(SQLiteDatabase aDb, int aId) {
        String value = null;
        Cursor cursorWithItem = aDb.query(DataBaseContract.TABLE_NAME,
                new String[]{DataBaseContract.VALUE},
                BaseColumns._ID + " = " + aId,
                null, null, null, null);
        if (cursorWithItem != null) {
            //Should be only one item.
            if (cursorWithItem.getCount() == 1) {
                cursorWithItem.moveToFirst();
                int valueColIndex = cursorWithItem.getColumnIndex(DataBaseContract.VALUE);
                value = cursorWithItem.getString(valueColIndex);
            }
            cursorWithItem.close();
        }
        return value;
    }

    /**
     * Send message to handler for adding new value to list.
     *
     * @param aValue new value.
     */
    private void sendMsgForAdd(String aValue) {
        Message message = mHandler.obtainMessage(HANDLER_MSG_ADD, aValue);
        message.sendToTarget();
    }

    /**
     * Send message to handler for moving item from one position to another.
     *
     * @param aFrom position from.
     * @param aTo position to.
     */
    private void sendMsgForMove(int aFrom, int aTo) {
        Message message = mHandler.obtainMessage(HANDLER_MSG_MOVE, aFrom, aTo);
        message.sendToTarget();
    }

}
