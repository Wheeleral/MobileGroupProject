package db.wpi.awheeler2.groupproject.cache;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Cache {
    private AnimalCache animalCache;
    private BitmapCache bitmapCache;

    public Cache(int aSize, int bSize) {
        this.animalCache = new AnimalCache(aSize);
        this.bitmapCache = new BitmapCache(bSize);
    }

    public void addBitmapToMemoryCache(String animal, String key, Bitmap bitmap) {
        ArrayList<String> keysToAnimalBitmaps = new ArrayList<>();
        ArrayList<String> keysInCache = getKeysOfAnimalInMemCache(animal);

        if (getBitmapFromMemCache(animal) == null) {
            this.bitmapCache.put(key, bitmap);

            if (keysInCache != null) {
                System.out.println("Keys in animal cache!");
                keysToAnimalBitmaps.addAll(keysInCache);
            }

            keysToAnimalBitmaps.add(key);

            this.animalCache.put(animal, keysToAnimalBitmaps);

            System.out.println("For animal " + animal + " we have the following keys: " + keysToAnimalBitmaps);
        }
    }

    private synchronized Bitmap  getBitmapFromMemCache(String key) {
        return this.bitmapCache.get(key);
    }


    /* Call this function after getAllBitmapsOfAnimalInMemoryCache
     */
    private synchronized ArrayList<String> getKeysOfAnimalInMemCache(String animal) {
        return this.animalCache.get(animal);
    }

    private synchronized void updateKeysInAnimalCache(String animal, ArrayList<String> keys) {
        this.animalCache.put(animal, keys);
    }

    /* Update AnimalCache as well!
     */
    public synchronized ArrayList<Bitmap> getAllBitmapsOfAnimalInMemoryCache(String animal) {
        ArrayList<String> keys = getKeysOfAnimalInMemCache(animal);
        ArrayList<Bitmap> bitmapsOfAnimalInCache = new ArrayList<>();
        ArrayList<String> newKeysToAnimalCache = new ArrayList<>();

        if (keys != null) {
            for (String key: keys) {
                Bitmap bitmap = getBitmapFromMemCache(key);
                if (bitmap != null) {
                    bitmapsOfAnimalInCache.add(bitmap);
                    newKeysToAnimalCache.add(key);
                }
            }

            updateKeysInAnimalCache(animal, newKeysToAnimalCache);
        }

        return bitmapsOfAnimalInCache;
    }

    public synchronized ArrayList<String> getValuesInAnimalCache(String animal) {
        return this.animalCache.get(animal);
    }

    public synchronized Bitmap getValuesInBitmapCache(String id) {
        return this.bitmapCache.get(id);
    }

    public BitmapCache getBitmapCache() {
        return this.bitmapCache;
    }

    public AnimalCache getAnimalCache() {
        return this.animalCache;
    }
}
