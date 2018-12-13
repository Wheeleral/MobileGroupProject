package db.wpi.awheeler2.groupproject;

import android.content.Context;

import db.wpi.awheeler2.groupproject.database.AnimalDB;

public class DB {
    private static AnimalDB instance;

    private DB() {}

    public static synchronized AnimalDB getInstance(Context context) {
        if (instance == null) {
            System.out.println("*******************CREATE ANOTHER INSTANCE *****************");
            instance = new AnimalDB(context);

            // Load data
            instance.saveImagesFromAsset(new String[]{"cat", "dog"});
        } else {
            System.out.println("*******************DO NOT NEED ANOTHER INSTANCE! *****************");
        }

        return instance;
    }
}
