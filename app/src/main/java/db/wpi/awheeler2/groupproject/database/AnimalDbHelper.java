package db.wpi.awheeler2.groupproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AnimalDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Animal.db";
    public static final int DATABASE_VERSION = 1;

    // Create and maintain DB
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AnimalContract.AnimalEntry.TABLE_NAME + " (" +
                    AnimalContract.AnimalEntry._ID + " INTEGER PRIMARY KEY," +
                    AnimalContract.AnimalEntry.COLUMN_NAME_TAG + " TEXT," +
                    AnimalContract.AnimalEntry.COLUMN_NAME_PATH + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AnimalContract.AnimalEntry.TABLE_NAME;

    // Default Constructor
    public AnimalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Discard existing data
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void clearDb(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
