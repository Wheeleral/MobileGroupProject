package db.wpi.awheeler2.groupproject.database;

import android.provider.BaseColumns;

public final class AnimalContract {
    // Ensure that this class does not get instantiated by the user
    private AnimalContract() {}

    // Define table contents in database
    public static class AnimalEntry implements BaseColumns {
        public static final String TABLE_NAME = "animal";
        public static final String COLUMN_NAME_TAG = "tag";
        public static final String COLUMN_NAME_PATH = "path";
    }


}
