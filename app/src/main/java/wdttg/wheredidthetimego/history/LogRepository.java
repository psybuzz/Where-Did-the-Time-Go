package wdttg.wheredidthetimego.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 11/9/2014.
 */
public class LogRepository implements Closeable{

    private SQLiteDatabase database;
    private LogDataHandler dataHandler;

    private String[] allColumns = { LogDataHandler.COLUMN_ID, LogDataHandler.COLUMN_BEGIN,
            LogDataHandler.COLUMN_END, LogDataHandler.COLUMN_PRODUCTIVITY};

    public LogRepository(Context context) {
        dataHandler = new LogDataHandler(context);
        database = dataHandler.getWritableDatabase();
    }




    @Override
    public void close() {
        database.close();
    }

    /**
     * Generates and stores a new log entry with the given parameters
     * @param begin
     * @param end
     * @param productivity
     * @return
     */
    public LogEntry createLogEntry(long begin, long end, Double productivity) {
        ContentValues values = new ContentValues();
        values.put(LogDataHandler.COLUMN_BEGIN, begin);
        values.put(LogDataHandler.COLUMN_END, end);
        values.put(LogDataHandler.COLUMN_PRODUCTIVITY, productivity);
        long insertId = database.insert(LogDataHandler.TABLE_LOGS, null, values);
        return getLogEntry(insertId);
    }

    public LogEntry updateLogEntry(long id, Double productivity) {
        ContentValues values = new ContentValues();
        values.put(LogDataHandler.COLUMN_PRODUCTIVITY, productivity);
        long updateId = database.update(LogDataHandler.TABLE_LOGS, values, LogDataHandler.COLUMN_ID + " = " + id, null);
        return getLogEntry(updateId);
    }

    public LogEntry getLogEntry(long id) {
        Cursor cursor = database.query(LogDataHandler.TABLE_LOGS, allColumns,
                LogDataHandler.COLUMN_ID + " = " + id, null, null, null, null);
        LogEntry gotten = null;
        if(cursor.moveToFirst()) {
            gotten = cursorToEntry(cursor);
        }
        cursor.close();
        return gotten;
    }

    public List<LogEntry> getEntriesBetween(long start, long end) {
        List<LogEntry> entries = new ArrayList<LogEntry>();

        Cursor cursor = database.query(LogDataHandler.TABLE_LOGS, allColumns,
                "(" + LogDataHandler.COLUMN_BEGIN + " >=  " + start + " AND " +
                        LogDataHandler.COLUMN_BEGIN + " <= " + end + ") OR (" +
                        LogDataHandler.COLUMN_END + " >=  " + start + " AND " +
                        LogDataHandler.COLUMN_END + " <= " + end + ")",
                null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            entries.add(cursorToEntry(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    /**
     *
     * @param start
     * @param end
     * @return null iff there are no entries in this span
     */
    public Float getAverageProductivityBetween(long start, long end) {
        List<LogEntry> entries = getEntriesBetween(start, end);

        float sum = 0;
        int numCounted = 0;
        for (LogEntry entry : entries) {
            if (entry.getProductivity() == null) {
                continue;
            }

            sum += entry.getProductivity();
            numCounted++;
        }

        if (numCounted == 0) {
            return null;
        }

        return sum / numCounted;
    }

    public List<LogEntry> unfilledEntriesBetween(long start, long end) {
        List<LogEntry> entries = getEntriesBetween(start, end);

        List<LogEntry> unfilled = new ArrayList<LogEntry>();
        for (LogEntry entry : entries) {
            if (entry.getProductivity() == null) {
                unfilled.add(entry);
            }
        }

        return unfilled;
    }

    public void clearTable() {
        database.execSQL("DELETE FROM " + LogDataHandler.TABLE_LOGS);
        database.execSQL("VACUUM");
    }

    private LogEntry cursorToEntry(Cursor cursor) {
        return new LogEntry(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2),
                cursor.isNull(3) ? null : cursor.getDouble(3));
    }


}
