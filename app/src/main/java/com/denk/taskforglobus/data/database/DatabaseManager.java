package com.denk.taskforglobus.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Singleton to control access to database.
 */
final class DatabaseManager {

    private static DatabaseManager sInstance;
    private static SQLiteOpenHelper sDatabaseHelper;
    private SQLiteDatabase mDatabase;

    /**
     * Private constructor.
     *
     * @param aContext {@link Context}.
     */
    private DatabaseManager(Context aContext) {
        sDatabaseHelper = new DBHelper(aContext);
    }

    /**
     * Initialize {@link DatabaseManager} for create instance.
     * <p>
     * Note: Should be called first of all.
     *
     * @param aContext {@link Context}.
     */
    public static synchronized void initializeInstance(Context aContext) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(aContext);
        }
    }

    /**
     * Returns {@link DatabaseManager} instance.
     * <p>
     * Note: Can be called after call {@link #initializeInstance(Context)}.
     *
     * @return instance of {@link DatabaseManager}.
     */
    public static synchronized DatabaseManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName()
                    + " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    /**
     * Returns {@link SQLiteDatabase} instance.
     * <p>
     * Note: Can be called after call {@link #initializeInstance(Context)}.
     * @return instance of {@link SQLiteDatabase}.
     */
    public synchronized SQLiteDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = sDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mDatabase != null) {
            mDatabase.close();
        }
        if (sDatabaseHelper != null) {
            sDatabaseHelper.close();
        }
        super.finalize();
    }

    /**
     * Inner SQLite helper for linked list data base.
     */
    final class DBHelper extends SQLiteOpenHelper {

        static final String DB_NAME = "appDB";
        static final int DB_VERSION = 1;

        /**
         * Private constructor.
         *
         * @param aContext {@link Context}.
         */
        private DBHelper(Context aContext) {
            super(aContext, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase aDb) {
            aDb.execSQL("create table " + DataBaseContract.TABLE_NAME + " ("
                    + BaseColumns._ID + " integer primary key autoincrement,"
                    + DataBaseContract.LINK + " integer,"
                    + DataBaseContract.VALUE + " text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase aDb, int aOldVersion, int aNewVersion) {
        }
    }
}