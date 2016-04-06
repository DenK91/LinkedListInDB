package com.denk.taskforglobus.data.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * {@link ContentProvider} for linked list data base.
 */
public class DataBaseProvider extends ContentProvider {

    static final String AUTHORITY = "g.testapp.denk.testappforg.providers.LinkedList";
    static final String LIST_PATH = "linked_list_table";

    /**
     * Uri to linked list content.
     */
    public static final Uri LIST_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + LIST_PATH);
    static final String LIST_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + LIST_PATH;
    static final String LIST_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + LIST_PATH;

    static final int URI_LIST = 1;
    static final int URI_LIST_ID = 2;
    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, LIST_PATH, URI_LIST);
        URI_MATCHER.addURI(AUTHORITY, LIST_PATH + "/#", URI_LIST_ID);
    }

    @Override
    public boolean onCreate() {
        DatabaseManager.initializeInstance(getContext());
        return true;
    }

    @Override
    public int delete(@NonNull Uri aUri, String aSelection, String[] aSelectionArgs) {
        switch (URI_MATCHER.match(aUri)) {
            case URI_LIST:
                break;
            case URI_LIST_ID:
                String id = aUri.getLastPathSegment();
                if (TextUtils.isEmpty(aSelection)) {
                    aSelection = BaseColumns._ID + " = " + id;
                } else {
                    aSelection = aSelection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + aUri);
        }
        int cnt = DatabaseManager.getInstance().getDatabase()
                .delete(DataBaseContract.TABLE_NAME, aSelection, aSelectionArgs);
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(aUri, null);
        }
        return cnt;
    }

    @Override
    public String getType(@NonNull Uri aUri) {
        switch (URI_MATCHER.match(aUri)) {
            case URI_LIST:
                return LIST_CONTENT_TYPE;
            case URI_LIST_ID:
                return LIST_CONTENT_ITEM_TYPE;
            default:
                break;
        }
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri aUri, ContentValues aValues) {
        if (URI_MATCHER.match(aUri) != URI_LIST) {
            throw new IllegalArgumentException("Wrong URI: " + aUri);
        }
        long rowID = DatabaseManager.getInstance().getDatabase()
                .insert(DataBaseContract.TABLE_NAME, null, aValues);
        Uri resultUri = ContentUris.withAppendedId(LIST_CONTENT_URI, rowID);
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(resultUri, null);
        }
        return resultUri;
    }

    @Override
    public Cursor query(@NonNull Uri aUri, String[] aProjection, String aSelection,
                        String[] aSelectionArgs, String aSortOrder) {
        switch (URI_MATCHER.match(aUri)) {
            case URI_LIST:
                break;
            case URI_LIST_ID:
                String id = aUri.getLastPathSegment();
                if (TextUtils.isEmpty(aSelection)) {
                    aSelection = BaseColumns._ID + " = " + id;
                } else {
                    aSelection = aSelection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + aUri);
        }
        Cursor cursor = DatabaseManager.getInstance().getDatabase()
                .query(DataBaseContract.TABLE_NAME, aProjection, aSelection,
                        aSelectionArgs, null, null, aSortOrder);
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), LIST_CONTENT_URI);
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri aUri, ContentValues aValues, String aSelection,
                      String[] aSelectionArgs) {
        switch (URI_MATCHER.match(aUri)) {
            case URI_LIST:
                break;
            case URI_LIST_ID:
                String id = aUri.getLastPathSegment();
                if (TextUtils.isEmpty(aSelection)) {
                    aSelection = BaseColumns._ID + " = " + id;
                } else {
                    aSelection = aSelection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + aUri);
        }
        int cnt = DatabaseManager.getInstance().getDatabase()
                .update(DataBaseContract.TABLE_NAME, aValues, aSelection, aSelectionArgs);
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(aUri, null);
        }
        return cnt;
    }
}