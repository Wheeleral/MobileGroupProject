package db.wpi.awheeler2.groupproject.database;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimalCache extends LruCache<String, Bitmap> {
    // Maps cache key (= primary keys of DB) to type of animal
    private HashMap<String, String> keyToAnimal;

    // Maps type of animal to a list of images identified by keys/ids
    private HashMap<String, ArrayList<String>> animalToKeys;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public AnimalCache(int maxSize) {
        super(maxSize);

        this.keyToAnimal = new HashMap<>();
        this.animalToKeys = new HashMap<>();
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        // The cache size will be measured in kilobytes rather than
        // number of items.
        return bitmap.getByteCount() / 1024;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        // Determine what animal it is removed
        String animal = this.keyToAnimal.get(key);

        // Remove from mapping
        this.keyToAnimal.remove(key);

        ArrayList<String> keys = this.animalToKeys.get(animal);

        // Remove from record of this type of animal
        if (keys != null) {
            keys.remove(key);
        }
    }

    public void addBitmapToMemCache(String animal, String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            this.keyToAnimal.put(key, animal);

            ArrayList<String> keys = this.animalToKeys.get(animal);

            if (keys != null) {
                keys.add(key);
                this.put(key, bitmap);
            } else {
                this.keyToAnimal.remove(key);
            }
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return this.get(key);
    }

    public ArrayList<Bitmap> getBitmapOfAnimalFromMemCache(String animal) {
        ArrayList<String> keys;
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        if (keyToAnimal.containsValue(animal)) {
            keys = this.animalToKeys.get(animal);

            if (keys != null) {
                for (String key : keys) {
                    bitmaps.add(this.get(key));
                }
            }
        }

        return bitmaps;
    }

    public ArrayList<String> getAllKeysOfAnimalInMemCache(String animal) {
        return this.animalToKeys.get(animal);
    }
}
