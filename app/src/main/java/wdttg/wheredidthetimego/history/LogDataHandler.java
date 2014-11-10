package wdttg.wheredidthetimego.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Handles the direct interfacing with our SQLLite database
 *
 * Created by Matthew on 11/9/2014.
 */
public class LogDataHandler extends SQLiteOpenHelper {

    public static final String TABLE_LOGS = "logs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BEGIN = "begin";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_PRODUCTIVITY = "productivity";

    private static final String DATABASE_NAME = "logs.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_LOGS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_BEGIN + " integer not null, " +
            COLUMN_END + " integer not null, " +
            COLUMN_PRODUCTIVITY + " real );";

    public LogDataHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        if (i != i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
            onCreate(sqLiteDatabase);
        }
    }
}
