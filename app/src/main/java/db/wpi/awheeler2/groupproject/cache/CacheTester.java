package db.wpi.awheeler2.groupproject.cache;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class CacheTester {
    private ArrayList<Bitmap> cats;
    private ArrayList<Bitmap> dogs;
    private Cache cache;

    public CacheTester(ArrayList<Bitmap> cats, ArrayList<Bitmap> dogs) {
        this.cats = cats;
        this.dogs = dogs;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        this.cache = new Cache(cacheSize, cacheSize);
    }

    public void runTests() {
        /* Test basic functionality of cache */
        addBitmap(cats.get(0));
        removeBitmap("cat", "cat1", cats.get(0));
        checkUpdateInAnimalCache("cat");

        /* Test for adding another animal */
        checkCacheForTwoAnimals();
        System.out.println("*********** CONGRATULATIONS! ALL TEST CASES PASSED! **********");
    }

    /* Test for adding bitmap to cache */
    private void addBitmap(Bitmap bitmap) {
        this.cache.addBitmapToMemoryCache("cat", "cat1", bitmap);

        if (this.cache.getValuesInAnimalCache("cat").size() != 1 || !this.cache.getValuesInAnimalCache("cat").get(0).equals("cat1")) {
            throw new AssertionError();
        }

        if (this.cache.getValuesInBitmapCache("cat1") != bitmap) {
            throw new AssertionError();
        }
    }

    /* Test for when bitmap is removed from bitmap cache */
    private void removeBitmap(String animal, String id, Bitmap bitmap) {
        // Clear the cache to mimic cache eviction of bitmap
        this.cache.getBitmapCache().evictAll();

        if (this.cache.getValuesInBitmapCache("cat1") != null) {
            throw new AssertionError();
        }

        // Animal cache does not update - still have old version of all ids in BitmapCache

        if (this.cache.getValuesInAnimalCache(animal).size() != 1 || !this.cache.getValuesInAnimalCache("cat").get(0).equals("cat1")) {
            throw new AssertionError();
        }

    }

    /* Animal not in animal cache - should add all everything */
    private void checkUpdateInAnimalCache(String animal) {
        // Get results
        ArrayList<Bitmap> result = this.cache.getAllBitmapsOfAnimalInMemoryCache(animal);

        if (result.size() > 0) {
            throw new AssertionError();
        }

        // Calling previous function should also update AnimalCache as well
        // Animal cache does not update - still have old version of all ids in BitmapCache
        // In animal cache:
        if (this.cache.getValuesInAnimalCache(animal).size() > 0) {
            throw new AssertionError();
        }
    }

    private void checkCacheForTwoAnimals() {
        this.cache.getAnimalCache().evictAll();

        if (this.cache.getValuesInAnimalCache("cat") != null) {
            throw new AssertionError();
        }

        if (this.cache.getValuesInAnimalCache("dog") != null) {
            throw new AssertionError();
        }

        this.cache.addBitmapToMemoryCache("cat", "cat1", cats.get(0));
        this.cache.addBitmapToMemoryCache("cat", "cat2", cats.get(1));
        this.cache.addBitmapToMemoryCache("dog", "dog1", dogs.get(0));

        if (this.cache.getValuesInAnimalCache("cat").size() != 2) {
            throw new AssertionError();
        }

        if (this.cache.getValuesInAnimalCache("dog").size() != 1) {
            throw new AssertionError();
        }
    }
}
