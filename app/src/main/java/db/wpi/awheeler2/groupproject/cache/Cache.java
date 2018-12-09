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
        if (getBitmapFromMemCache(animal) == null) {
            this.bitmapCache.put(key, bitmap);

            ArrayList<String> keysToAnimalBitmaps = new ArrayList<>(getKeysOfAnimalInMemCache(animal));
            keysToAnimalBitmaps.add(key);

            this.animalCache.put(animal, keysToAnimalBitmaps);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return this.bitmapCache.get(key);
    }

    public ArrayList<String> getKeysOfAnimalInMemCache(String animal) {
        return this.animalCache.get(animal);
    }

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
}
