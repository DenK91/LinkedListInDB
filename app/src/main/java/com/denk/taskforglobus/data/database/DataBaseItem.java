package com.denk.taskforglobus.data.database;

/**
 * Represents one item from database.
 */
public class DataBaseItem {
    private final long mId;
    private String mValue;

    /**
     * Constructor.
     *
     * @param aId item id.
     * @param aValue item value.
     */
    DataBaseItem(long aId, String aValue) {
        mId = aId;
        mValue = aValue;
    }

    /**
     * Gets id of item.
     *
     * @return id of item.
     */
    public long getId() {
        return mId;
    }

    /**
     * Gets value of item.
     *
     * @return value of item.
     */
    public String getValue() {
        return mValue;
    }

    @Override
    public boolean equals(Object aObj) {
        if (this == aObj) {
            return true;
        }
        if (aObj == null || getClass() != aObj.getClass()) {
            return false;
        }
        DataBaseItem item = (DataBaseItem)aObj;
        return this.mId == item.mId && this.mValue.equals(item.mValue);
    }

    @Override
    public int hashCode() {
        int result = (int) mId;
        result = 31 * result + (mValue != null ? mValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "id=" + mId + " value=" + mValue;
    }
}
