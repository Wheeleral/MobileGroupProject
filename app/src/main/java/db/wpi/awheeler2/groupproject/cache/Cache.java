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

        if (getBitmapFromMemCache(animal) == null) {
            this.bitmapCache.put(key, bitmap);

            if (getKeysOfAnimalInMemCache(animal) != null) {
                keysToAnimalBitmaps.addAll(getKeysOfAnimalInMemCache(animal));
            }

            keysToAnimalBitmaps.add(key);

            this.animalCache.put(animal, keysToAnimalBitmaps);

            System.out.println("For animal " + animal + " we have the following keys: " + keysToAnimalBitmaps);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return this.bitmapCache.get(key);
    }


    /* Call this function after getAllBitmapsOfAnimalInMemoryCache
     */
    public ArrayList<String> getKeysOfAnimalInMemCache(String animal) {
        return this.animalCache.get(animal);
    }

    /* Update AnimalCache as well!
     */
    public  ArrayList<Bitmap> getAllBitmapsOfAnimalInMemoryCache(String animal) {
        ArrayList<String> keys = getKeysOfAnimalInMemCache(animal);
        ArrayList<Bitmap> bitmapsOfAnimalInCache = new ArrayList<>();

        if (keys != null) {
            for (String key: keys) {
                Bitmap bitmap = getBitmapFromMemCache(key);
                if (bitmap == null) {
                    keys.remove(key);
                } else {
                    bitmapsOfAnimalInCache.add(bitmap);
                }
            }

            this.animalCache.put(animal, keys);
        }

        return bitmapsOfAnimalInCache;
    }

    public ArrayList<String> getValuesInAnimalCache(String animal) {
        return this.animalCache.get(animal);
    }

    public Bitmap getValuesInBitmapCache(String id) {
        return this.bitmapCache.get(id);
    }

    public BitmapCache getBitmapCache() {
        return this.bitmapCache;
    }

    public AnimalCache getAnimalCache() {
        return this.animalCache;
    }
}
