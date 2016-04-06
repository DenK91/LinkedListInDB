package com.denk.taskforglobus.data.database;

/**
 * Contract class for data base.
 * <p>
 * Should be in external shared lib.
 */
public final class DataBaseContract {

    /**
     * Table name for linked list.
     */
    public static final String TABLE_NAME = "linked_list_table";

    /**
     * Column name of link in the linked list table of data base.
     * <p>
     * Type: Integer.
     */
    public static final String LINK = "linkToNext";

    /**
     * Column name of value in the linked list table of data base.
     * <p>
     * Type: String.
     */
    public static final String VALUE = "value";

    /**
     * Private constructor.
     */
    private DataBaseContract() {

    }
}
