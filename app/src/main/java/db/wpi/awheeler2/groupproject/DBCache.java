package db.wpi.awheeler2.groupproject;

import android.content.Context;

import db.wpi.awheeler2.groupproject.cache.AnimalDBCache;

public class DBCache {
    private static AnimalDBCache instance;

    private DBCache() {}

    public static AnimalDBCache getInstance(Context context) {
        if (instance == null) {
            System.out.println("*******************CREATE ANOTHER INSTANCE *****************");
            instance = new AnimalDBCache(context);

            // Load data
            instance.saveImagesFromAsset(new String[]{"cat", "dog"});
        } else {
            System.out.println("*******************DO NOT NEED ANOTHER INSTANCE! *****************");
        }

        return instance;
    }
}
